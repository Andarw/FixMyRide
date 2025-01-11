import flask
import werkzeug
import numpy as np
import json
from statistics import mode
from inference import DamageHandler
from pricelist import get_price_list


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
            print(damage_prediction)
            if damage_prediction not in damage_predictions and damage_prediction != 'no_damage':
                if '_' in damage_prediction:
                    damage_prediction = damage_prediction.replace('_', ' ')
                damage_prediction = damage_prediction.title()
                damage_predictions += damage_prediction + ","

            brand_prediction = handlerBrands.predict(fpath)
            print(brand_prediction)
            brand_predictions.append(brand_prediction)
            i += 1
        print(brand_predictions)
        brand_prediction = mode(brand_predictions).title()
        if 'Mercedes' not in brand_prediction:
            brand_prediction = brand_prediction.split('-')
        else:
            brand_prediction = ['Mercedes-benz', 'Sklass']
        if len(damage_predictions) == 0:
            damage_predictions = 'No Damage,'
        print(brand_prediction[0] + '_' + brand_prediction[1] + '_' + damage_predictions)
        return brand_prediction[0] + '_' + brand_prediction[1] + '_' + damage_predictions
    else:
        brand_name = flask.request.form.get('selectedBrand')
        model_name = flask.request.form.get('selectedModel')    
        model_year = flask.request.form.get('selectedYear') 
        damage = flask.request.form.get('selectedTags')
        print("Thr request received is: ", brand_name, model_name, model_year, damage)
        model = brand_name + '-' + model_name
        print("The model is: ", model)
        damage_list = [item.strip() for item in damage.split(',')]
        print("The damage is: ", damage_list)
        report = get_price_list(model, int(model_year), damage_list=damage_list)
        full_model = model_name + " " + report["ModelCode"]
        price = str(report["TotalMin"]) + "-" + str(report["TotalMax"]) + " RON"
        links = ", ".join(report["Links"])
        response =  full_model + "," + price + "," + links
        print(response)
        return response


app.run(host="0.0.0.0", port=5000, debug=True)

