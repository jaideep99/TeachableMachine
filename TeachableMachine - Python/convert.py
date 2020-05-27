import tensorflow as tf
import numpy as np

def convert(name):
    model = tf.keras.models.load_model(name+".h5")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    # Convert the model
    tflite_model = converter.convert()
    # Create the tflite model file
    tflite_model_name = name+".tflite"
    open(tflite_model_name, "wb").write(tflite_model)

    interpreter = tf.lite.Interpreter(model_path=name+".tflite")
    interpreter.allocate_tensors()

    # Get input and output tensors
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    # Test model on random input data
    input_shape = input_details[0]['shape']

    # print(input_details[0]['index'])

    input_data = np.array(np.random.random_sample(input_shape), dtype=np.float32)
    interpreter.set_tensor(input_details[0]['index'], input_data)
    interpreter.invoke()

    # print(output_details[0]['index'])
    output_data = interpreter.get_tensor(output_details[0]['index'])
    # print(output_data)