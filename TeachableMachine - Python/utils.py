import numpy as np
from PIL import Image
import matplotlib.pyplot as plt
from skimage.transform import rotate, AffineTransform, warp
from scipy.ndimage import zoom
import cv2
import firebase_admin
from firebase_admin import credentials
from firebase_admin import storage

def clipped_zoom(img, zoom_factor, **kwargs):

    h, w = img.shape[:2]

    zoom_tuple = (zoom_factor,) * 2 + (1,) * (img.ndim - 2)

    # Zooming out
    if zoom_factor < 1:

        # Bounding box of the zoomed-out image within the output array
        zh = int(np.round(h * zoom_factor))
        zw = int(np.round(w * zoom_factor))
        top = (h - zh) // 2
        left = (w - zw) // 2

        # Zero-padding
        out = np.zeros_like(img)
        out[top:top+zh, left:left+zw] = zoom(img, zoom_tuple, **kwargs)

    # Zooming in
    elif zoom_factor > 1:

        # Bounding box of the zoomed-in region within the input array
        zh = int(np.round(h / zoom_factor))
        zw = int(np.round(w / zoom_factor))
        top = (h - zh) // 2
        left = (w - zw) // 2

        out = zoom(img[top:top+zh, left:left+zw], zoom_tuple, **kwargs)

        trim_top = ((out.shape[0] - h) // 2)
        trim_left = ((out.shape[1] - w) // 2)
        out = out[trim_top:trim_top+h, trim_left:trim_left+w]

    # If zoom_factor == 1, just return the input array
    else:
        out = img
    return out

def augment_image():

    data = []
    img = cv2.imread('cache/take.png')
    img = np.array(img, dtype = np.uint8)
    img = img/255
    
    data.append(img)
    
    #flipping image
    flipped_img = np.fliplr(img)
    data.append(flipped_img)

    #rotate imagee 90
    rt = rotate(img, angle=90)
    data.append(rt)

   
    #Zoom Image
    zom = clipped_zoom(img,1.5)
    data.append(zom)

    # cv2.imshow("flipped",zom)
    # cv2.waitKey(0)

    return data

def push_model(project,Uid,ptype):
    cred = credentials.Certificate('serviceAccountCredentials.json')
    app = firebase_admin.initialize_app(cred, {
        'storageBucket': 'teachable-machine-eb5f7.appspot.com'
    },name='storage')

    storage_bucket = storage.bucket(app=app)

    blob = storage_bucket.blob(Uid+"/"+ptype+"/"+project+"/"+"model/"+project+".tflite")

    blob.upload_from_filename(project+".tflite")


