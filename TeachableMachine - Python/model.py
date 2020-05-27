import tensorflow as tf
from keras import Sequential
from keras.layers import Dense
from keras.optimizers import Adam
from keras.layers import Conv2D,MaxPooling2D,Flatten
from keras.losses import SparseCategoricalCrossentropy
import numpy as np
from sklearn.metrics import accuracy_score,confusion_matrix,f1_score
import matplotlib.pyplot as plt
from convert import convert
from utils import push_model

def CNN(x_train,x_test,y_train,y_test,epochs,batch,learning_rate,n,project,uid,ptype):

    model = Sequential()
    
    model.add(Conv2D(32,(3,3),activation='relu',input_shape=(256,256,3)))
    model.add(MaxPooling2D((2,2)))

    model.add(Conv2D(64,(3,3),activation='relu'))
    model.add(MaxPooling2D((2,2)))

    model.add(Conv2D(128,(3,3),activation='relu'))
    model.add(MaxPooling2D((2,2)))

    model.add(Conv2D(128,(3,3),activation='relu'))
    model.add(MaxPooling2D((2,2)))

    model.add(Flatten())
    model.add(Dense(512,activation='relu'))
    model.add(Dense(n,activation='softmax'))

    model.compile(Adam(learning_rate=learning_rate),loss='categorical_crossentropy',metrics=['accuracy'])

    history = model.fit(x_train,y_train,epochs=epochs,batch_size=batch,verbose=1)

    predicted = model.predict(x_test)

    predicted = np.argmax(predicted,axis=1)
    y_test = np.argmax(y_test,axis=1)

    print("Accuracy score : ",str(accuracy_score(y_test,predicted)))
    print("F1 score : ",str(f1_score(y_test,predicted,zero_division=1)))
    print("Confusion Matrix : ")
    print(confusion_matrix(y_test,predicted))

    losses = history.history['loss']

    plt.plot(losses)
    plt.show()

    print("Saving Model ....\n")
    model.save(project+".h5")
    print("Conerting Model to .tflite ....\n") 
    convert(project)
    print("pushing model to cloud...\n")
    push_model(project,uid,ptype)
