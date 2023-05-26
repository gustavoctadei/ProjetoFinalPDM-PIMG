import flask
import werkzeug

from flask import send_file

app = flask.Flask(__name__)

@app.route('/', methods = ['GET', 'POST'])
def handle_request():
    imagefile = flask.request.files['image']
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    print("\nReceived image File name : " + imagefile.filename)
    imagefile.save(filename)
    
    return send_file("return.jpg", mimetype='image/jpg')

app.run(host="0.0.0.0", port=5000, debug=True)