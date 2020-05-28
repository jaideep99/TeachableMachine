# import tensorflow as tf
# import numpy as np
# import cv2
# from PIL import Image

# model = tf.keras.models.load_model("Fruits.h5")

# img = cv2.imread('cache/cache2.jpg')
# img = np.array(img, dtype = np.int)
# img = img/255

# # print(img)
# cv2.imshow("hello",img)
# cv2.waitKey(0)
# for i in range(256):
#     for j in range(256):
#         for k in range(3):
#             print(str(i)+" : "+str(j)+" : "+str(k)+" : "+str(img[i][k][k]))
#         print()
#     print()

# print(img[112][121][0],img[121][112][0],img[112][122][0])

# inp = np.array([img])

# print(model.predict(inp))

