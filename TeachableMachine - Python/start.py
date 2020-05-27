from dataset import make_dataset
import numpy as np
from sklearn.model_selection import train_test_split
from model import CNN
import pandas as pd

def make_model(uid,ptype,epochs,batch,learning_rate,project,classes):
    
    #retriveing data after Image augmentation
    data,labels = make_dataset(uid,ptype,project,classes)

    data = np.array(data)
    
    #one-hot encoding
    labels = pd.Series(labels)
    labels = pd.get_dummies(labels).values

    x_train,x_test,y_train,y_test = train_test_split(data,labels,test_size=0.2,random_state = 42)
    
    
    CNN(x_train,x_test,y_train,y_test,int(epochs),int(batch),float(learning_rate),len(classes),project,uid,ptype)

    

