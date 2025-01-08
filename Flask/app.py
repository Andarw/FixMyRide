import flask
import werkzeug
import numpy as np
import json
from statistics import mode
from inference import DamageHandler

ROOT = 'C:/Users/HP/Desktop/AN-IV-SEM-I/MSA/Flask/Flask'
DAMAGE_MODEL_PATH = ROOT + '/damage/14.pth'
BRANDS_MODEL_PATH = ROOT + '/brands/14.pth'

#load numpy array with the classes
damageClasses = np.load(ROOT + '/resources/damageClasses.npy')
brandsClasses = np.load(ROOT + '/resources/brandsClasses.npy')
handlerDamage = DamageHandler(DAMAGE_MODEL_PATH, damageClasses)
handlerBrands = DamageHandler(BRANDS_MODEL_PATH, brandsClasses)

app = flask.Flask(__name__)
@app.route('/', methods = ['GET', 'POST'])

def handle_request():
    print(damageClasses)
    print(brandsClasses)
    has_image = flask.request.form.get('hasImage', '').lower() == 'true'
    if has_image:

        if 'image0' not in flask.request.files:
            return "Error: hasImage is true, but no image was provided.", 400

        i = 0
        damage_predictions = ""
        brand_predictions = []
        while True:
            fname = 'image' + str(i)
            if fname not in flask.request.files:
                break
            imagefile = flask.request.files[fname]
            filename = werkzeug.utils.secure_filename(imagefile.filename)
            print("\nReceived image File name : " + imagefile.filename)
            fpath = ROOT + "/image_requests/" + filename
            imagefile.save(fpath)
            damage_prediction = handlerDamage.predict(fpath)
            if damage_prediction not in damage_predictions:
                if '_' in damage_prediction:
                    damage_prediction = damage_prediction.replace('_', ' ')
                damage_prediction = damage_prediction.title()
                damage_predictions += damage_prediction + ","

            brand_prediction = handlerBrands.predict(fpath)
            brand_predictions.append(brand_prediction)
            i += 1
        print(brand_predictions)
        brand_prediction = mode(brand_predictions).title()
        if 'MERCEDES' not in brand_prediction:
            brand_prediction = brand_prediction.split('-')
        else:
            brand_prediction = ['MERCEDES-BENZ', 'SKLASS']
        if len(damage_predictions) == 0:
            damage_predictions = 'No Damage,'
        print(brand_prediction[0] + '-' + brand_prediction[1] + '-' + damage_predictions)
        return brand_prediction[0] + '-' + brand_prediction[1] + '-' + damage_predictions
    else:
        brand_name = flask.request.form.get('selectedBrand')
        model_name = flask.request.form.get('selectedModel')    
        model_year = flask.request.form.get('selectedYear') 
        carid_link = "https://www.carid.com/"+ model_year + "-" + brand_name + "-" + model_name + "-accessories/" 
        autozone_link = "https://www.autozone.com/parts/collision-body-parts-and-hardware/" + brand_name.lower() + "/" + model_name.lower()
        return "9999.99," + carid_link + "," + autozone_link


app.run(host="0.0.0.0", port=5000, debug=True)

