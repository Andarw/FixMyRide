import flask
import werkzeug
import numpy as np
import json
from inference import DamageHandler

ROOT = 'C:/Users/Andre/Documents/CVC_Project'
MODEL_PATH = ROOT + '/results/exp/checkpoints/16.pth'

# load numpy array with the classes
classes = np.load(ROOT + '/resources/classes.npy')
handler = DamageHandler(MODEL_PATH, classes)

app = flask.Flask(__name__)
@app.route('/', methods = ['GET', 'POST'])

def handle_request():
    imagefile = flask.request.files['image']
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    print("\nReceived image File name : " + imagefile.filename)
    imagefile.save(filename)

    #get predictions
    prediction = handler.predict(filename)
    return " ".join(prediction.split('_'))

app.run(host="0.0.0.0", port=5000, debug=True)