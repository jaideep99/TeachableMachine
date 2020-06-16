# Teachable Machine
Mobile Version of Teachable Machine

__Motive__

The motive behind the project is the web-based teachable machine made by Google. We have developed the mobile version of the teachable machine to reach android users which improves both portability and ease of image capture through mobile. 

* Our intuition is, we can create better image/audio datasets using mobile, than on a PC(web based). We can show a great variance in datsets created using smartphone.

* Users can create projects and create classes in it.
* They can add images through instant camera or from gallery/storage.
* Once they create datasets, they can train models and also can set hyper parameters.
* Once user request to train model, a message is sent to server using sockets, then server parse message and retrieve data from firebase.
* The Images undergo process of Data Augmentation and are trained using CNN.
* Weights of the model are outputed as .h5 file and it is converted to .tflite file and this file is pushed to firebase storage.
* They can also test the model they have created, right itself on their mobile.
* When user requests for testing, the latest trained model is used, if the latest model(.tflite file) doesn't exist on local storage, then it is retrieved from firebase storage.

__Note__

* Currently we are using Local Server which is created using sockets(socket programming) as this just a prototype. We are trying to upgrade this to GCP (Google Cloud Platform) or AWS(Amazon Web Services).

* Requirements to run server are noted below!

__Flow Chart__

<img src="Images/flowchart.png">

__Technology Used__

1. Android(Java)
2. Convolutional Neural Networks
3. Keras
4. Data Augmentation
5. Socket Programming

__App Interface__

| Home Page | Projects |  Classes |
| ------------- | ------------- | ------------- |
| <img src="Images/homepage.jpg" width=220 height=441> | <img src="Images/projects.jpg" width=220 height=441> | <img src="Images/classes.jpg" width=220 height=441> |

| Dataset  | Train | Testing |
| ------------- | ------------- | ------------- |
| <img src="Images/dataset.jpg" width=220 height=441> | <img src="Images/train.jpg" width=220 height=441> | <img src="Images/testing.jpg" width=220 height=441> |

__Libraries to be installed to run Local Server__

* Numpy
* Pandas
* Firebase_admin
* OpenCV
* PIL
* Tensorflow
* Keras

__Note__

* 20Mbps required to maintain server( Created using sockets)

__Terminal ( Server Verbose )__

__Socket Connection__

<p align=center>
<img src="Images/server.png">
</p>

__Model Results__

<p align=center>
<img src="Images/result.png">
</p>

__APK__ file is present in debug folder.
