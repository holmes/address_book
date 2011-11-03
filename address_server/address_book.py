# all the imports
from sqlite3 import dbapi2 as sqlite3
from contextlib import closing
from flask import Flask, request, session, g, redirect, url_for, abort, \
     render_template, flash

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


# apparently there's a way to break this out, but I'm just going to keep this simple for now
@app.route('/')
def hello_world():
    return 'And boom, there goes the dynamite!'

@app.route('/addresses')
def addresses():
	return "getting all addresses"

@app.route('/address/<int:address_id>')
def address(address_id):
	return "address id is {0}".format(address_id)

@app.route('/address/<int:address_id>', methods=['POST'])
def new_address(address_id):
	return "saving address, id is {0}".format(address_id)

@app.route('/address/<int:address_id>', methods=['PUT'])
def edit_nickname(address_id):
	return "Updated nickname on {0} to {1}".format(address_id, address_id)
	
@app.route('/address/<int:address_id>', methods=['DELETE'])
def delete_address(address_id):
	return "Address {0} is being deleted".format(address_id)

if __name__ == '__main__':
	app.run(host='0.0.0.0')