# all the imports
from sqlite3 import dbapi2 as sqlite3
from contextlib import closing
from flask import Flask, request, session, g, jsonify, url_for, abort

# configuration
DATABASE = '/tmp/address_book.db'
DEBUG = True
SECRET_KEY = 'SECRET'
USERNAME = 'admin'
PASSWORD = 'default'

# create our little application :)
app = Flask(__name__)
app.config.from_object(__name__)

def connect_db():
    """Returns a new connection to the database."""
    return sqlite3.connect(app.config['DATABASE'])


def init_db():
    """Creates the database tables."""
    with closing(connect_db()) as db:
        with app.open_resource('schema.sql') as f:
            db.cursor().executescript(f.read())
        db.commit()

	
@app.before_request
def before_request():
    """Make sure we are connected to the database each request."""
    g.db = connect_db()


@app.teardown_request
def teardown_request(exception):
    """Closes the database again at the end of the request."""
    if hasattr(g, 'db'):
        g.db.close()

def query_db(query, args=(), one=False):
    cur = g.db.execute(query, args)
    rv = [dict((cur.description[idx][0], value)
            for idx, value in enumerate(row)) for row in cur.fetchall()]
    return (rv[0] if rv else None) if one else rv


# apparently there's a way to break this out, but I'm just going to keep this simple for now
@app.route('/')
def addresses():
    entries = query_db('select * from addresses order by id desc', one=False)
    return jsonify(items=entries)

@app.route('/address/<int:address_id>')
def address(address_id):
    address = query_db('select * from addresses where id = ?', [address_id], one=True)
    if address is None:
        return jsonify()
    else:
        return create_json(address)

@app.route('/address', methods=['POST'])
def new_address():
    query_db('insert into addresses (address, nickname, latitude, longitude) values (?, ?, 1, 1)',
                     [request.form['address'], request.form['nickname']], one=True)
    g.db.commit()
    address = query_db('select * from addresses order by id desc', one=True)
    return create_json(address)

@app.route('/address/<int:address_id>', methods=['PUT'])
def edit_nickname(address_id):
	query_db('update addresses set nickname = ? where id = ?', [request.form['nickname'], address_id])
	g.db.commit()
	return address(address_id)
	
@app.route('/address/<int:address_id>', methods=['DELETE'])
def delete_address(address_id):
	query_db('delete from addresses where id = ?', [address_id])
	g.db.commit()
	return jsonify(id=address_id)


def create_json(address_dict):
	# i guess you can just do it this way. who'd have thunk
    return jsonify(address_dict)


if __name__ == '__main__':
	app.run(host='0.0.0.0')