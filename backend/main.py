import flask
import werkzeug
import os

from flask import send_file
from image_util import *
from filtro_bilateral import *

app = flask.Flask(__name__)

@app.route('/', methods = ['POST'])
def handle_request():
    imagefile = flask.request.files['image']
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    imagefile.save(filename)

    img_out = carregar_imagem(imagefile.filename)
    os.remove(imagefile.filename)

    img_out = filtro_bilateral(img_out)
    #img_out = filtro_bilateral_opencv(img_out) #Utilizando o Filtro Bilateral da Biblioteca OpenCV
    cv2.imwrite('img_out.jpg', img_out)
    
    return send_file('img_out.jpg', mimetype='image/jpg')

app.run(host="0.0.0.0", port=5000, debug=True)