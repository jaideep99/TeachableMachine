import numpy as np
import pandas as pd
import firebase_admin
from firebase_admin import credentials
from firebase_admin import storage
from utils import augment_image

cred = credentials.Certificate('serviceAccountCredentials.json')
app = firebase_admin.initialize_app(cred, {
    'storageBucket': 'teachable-machine-eb5f7.appspot.com'
},name='storage')

storage_bucket = storage.bucket(app=app)

def make_dataset(uid,ptype,project,classes):
    
    inputs = []
    labels = []


    path = uid+'/'+ptype+'/'+project+'/'
    clss = 0
    for pclass in classes:
        tpath = path+pclass
        bucket = storage_bucket.list_blobs(prefix = tpath)
        print("Loading dataset : "+pclass)
        for x in bucket:
            x.download_to_filename('cache/take.png')
            k = augment_image()
            inputs.extend(k)
            for i in range(len(k)):
                labels.append(clss)
        clss+=1

    return inputs,labels


