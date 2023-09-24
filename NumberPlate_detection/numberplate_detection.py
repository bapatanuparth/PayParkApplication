import os
import cv2
import numpy as np
import tensorflow as tf
import sys
import pytesseract
import string
import socket

from utils import label_map_util
from utils import visualization_utils as vis_util

def send_str(str):
    pass

def preprocess(img,flag):
    gray=cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)

    if flag=="thresh":
        gray=cv2.threshold(gray, 0, 255,cv2.THRESH_BINARY|cv2.THRESH_OTSU)[1]
    elif flag=="blur":
    	gray=cv2.medianBlur(gray, 3)

    return gray

def apply_ocr(img):
    pytesseract.pytesseract.tesseract_cmd='C:/Program Files (x86)/Tesseract-OCR/tesseract.exe'
    tessdata_dir_config='--tessdata-dir "C:\\Program Files (x86)\\Tesseract-OCR\\tessdata"'
    text=pytesseract.image_to_string(img,config=tessdata_dir_config)
    text=text.translate({ord(c): None for c in string.whitespace})
    fil_text=''
    for char in text:
        if ord(char)>=48 and ord(char)<=57:
            fil_text+=char
        elif ord(char)>=65 and ord(char)<=90:
            fil_text+=char
    print(fil_text)

def validate_numberplate(array):
    pass

MODEL_NAME = 'inference_graph'
CWD_PATH = os.getcwd()
PATH_TO_CKPT = os.path.join(CWD_PATH,MODEL_NAME,'frozen_inference_graph.pb')
PATH_TO_LABELS = os.path.join(CWD_PATH,'training','labelmap.pbtxt')

## Load the label map
label_map = label_map_util.load_labelmap(PATH_TO_LABELS)
categories = label_map_util.convert_label_map_to_categories(label_map, max_num_classes=1, use_display_name=True)
category_index = label_map_util.create_category_index(categories)

# Load the Tensorflow model into memory.
detection_graph = tf.Graph()
with detection_graph.as_default():
    od_graph_def = tf.GraphDef()
    with tf.gfile.GFile(PATH_TO_CKPT, 'rb') as fid:
        serialized_graph = fid.read()
        od_graph_def.ParseFromString(serialized_graph)
        tf.import_graph_def(od_graph_def, name='')

    sess = tf.Session(graph=detection_graph)

#Input tensor as image
image_tensor = detection_graph.get_tensor_by_name('image_tensor:0')

# Output tensors are the detection boxes, scores, and classes
detection_boxes = detection_graph.get_tensor_by_name('detection_boxes:0')

# Each score represents level of confidence for each of the objects.
# The score is shown on the result image, together with the class label.
detection_scores = detection_graph.get_tensor_by_name('detection_scores:0')
detection_classes = detection_graph.get_tensor_by_name('detection_classes:0')

# Number of objects detected
num_detections = detection_graph.get_tensor_by_name('num_detections:0')

# Initialize webcam feed
video = cv2.VideoCapture(0)
ret = video.set(3,1280)
ret = video.set(4,720)

while(True):

    # Acquire frame and expand frame dimensions to have shape: [1, None, None, 3]
    # i.e. a single-column array, where each item in the column has the pixel RGB value
    ret, frame = video.read()
    frame_expanded = np.expand_dims(frame, axis=0)

    # Perform the detection by running the model with the image as input
    (boxes, scores, classes, num) = sess.run(
        [detection_boxes, detection_scores, detection_classes, num_detections],
        feed_dict={image_tensor: frame_expanded})

    # Draw the results of the detection
    vis_util.visualize_boxes_and_labels_on_image_array(
        frame,
        np.squeeze(boxes),
        np.squeeze(classes).astype(np.int32),
        np.squeeze(scores),
        category_index,
        use_normalized_coordinates=True,
        line_thickness=8,
        min_score_thresh=0.60)

    if scores[0][0]>0.6:
        height,width,ch=frame.shape
        ymin=int((boxes[0][0][0]*height))
        xmin=int((boxes[0][0][1]*width))
        ymax=int((boxes[0][0][2]*height))
        xmax=int((boxes[0][0][3]*width))

        Result=np.array(frame[ymin:ymax,xmin:xmax])
        #print(ymin,xmin,ymax,xmax)
        raw_img=preprocess(Result,"thresh")
        apply_ocr(raw_img)
        #display the results
        cv2.namedWindow('Result',flags=cv2.WINDOW_NORMAL)
        cv2.imshow('Result',raw_img)
    #cv2.imshow('Stream',frame)
    if cv2.waitKey(1) == ord('q'):
        break

video.release()
cv2.destroyAllWindows()
