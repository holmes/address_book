from flask import Flask
app = Flask(__name__)

@app.route('/')
def hello_world():
    return 'And boom, there goes the dynamite!'

if __name__ == '__main__':
    app.run(debug=True)