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

def Rect_area(w,h):
    #print(abs(h-w))
    if abs(h-w)<180:
        return w*h
    else:
        return 0

def preprocess(img,flag):
    gray=cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    gray=cv2.resize(gray,None,fx=1/2, fy=1/2,interpolation=cv2.INTER_AREA)
    print(gray.shape)
    if flag=="thresh":
        gray=cv2.medianBlur(gray, 3)
        gray=cv2.threshold(gray, 0, 255,cv2.THRESH_BINARY_INV|cv2.THRESH_OTSU)[1]
        #gray=cv2.adaptiveThreshold(gray,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY_INV,11,2)
        gray=cv2.morphologyEx(gray, cv2.MORPH_OPEN, None)
        gray=cv2.medianBlur(gray, 3)
    elif flag=="blur":
    	gray=cv2.medianBlur(gray, 3)

    return gray

def character_seperator(img):
    dim=img.shape

    x=h/dim[0]
    y=w/dim[1]

    img=cv2.resize(img,None,fx=x, fy=y, interpolation = cv2.INTER_CUBIC)  #Resize image to 550x250
    fil_img=cv2.fastNlMeansDenoisingColored(img,None,40,40,7,21)      #filter coloured image
    grey=cv2.cvtColor(fil_img,cv2.COLOR_BGR2GRAY)                         #Convert into grayscale
    _,thresh=cv2.threshold(grey,127,255,0)                        #threshold the image
    grey_img_blur=cv2.medianBlur(thresh,5)                        #apply median blur
    erode=cv2.erode(grey_img_blur, None, iterations=2)
    dilate=cv2.dilate(erode, None, iterations=3)
    erode=cv2.erode(dilate, None, iterations=1)         #apply erosion and dilation to highlight the characters

    flag,contours,hierarchy=cv2.findContours(erode,cv2.RETR_LIST,cv2.CHAIN_APPROX_SIMPLE) #Finding  the contours
    i=0
    img_array=[]
    for cnt in contours:
        if cv2.contourArea(cnt)>1000:        #Filter out the comtours by limiting the contour area
            #con.append(cnt)
            x1,y1,w1,h1=cv2.boundingRect(cnt)
            if Rect_area(w1,h1)>1700:# and Rect_area(w1,h1)<10000:
                #img=cv2.rectangle(img,(x1+w1,y1+h1),(x1,y1),(0,255,0),2)    #Draw bounding box
                #cv2.imshow(str(i),img[y1:y1+h1,x1:x1+w1])
                crop=img[y1:y1+h1,x1:x1+w1]
                crop=cv2.copyMakeBorder(crop,4,4,4,4,cv2.BORDER_CONSTANT,value=(255,255,255))
                dim_=crop.shape
                x_=400/dim_[0]
                y_=400/dim_[1]
                img_array.append(cv2.resize(crop,None,fx=y_, fy=x_,interpolation=cv2.INTER_CUBIC))
                #i+=1
    return img_array

def apply_ocr(img):
    #img_array=character_seperator(img)
    #img=combine(img_array)
    #cv2.namedWindow('Array',flags=cv2.WINDOW_NORMAL)
    #cv2.imshow("Array",img)
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
def combine(array):
   for i,p in enumerate(array):
       if i==0:
           a=p
       else:
            b=p
            a=np.hstack((a,b))
   return a

MODEL_NAME = 'inference_graph'
PATH_TO_IMAGE = 'C:/Users/Rushikesh/Desktop/Number_Plates/IMG_20181126_132725.jpg'
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


# Acquire frame and expand frame dimensions to have shape: [1, None, None, 3]
# i.e. a single-column array, where each item in the column has the pixel RGB value
h=720
w=1280
frame=cv2.imread(PATH_TO_IMAGE)
dim=frame.shape
x=h/dim[0]
y=w/dim[1]
frame=cv2.resize(frame,None, fx=y, fy=x, interpolation = cv2.INTER_CUBIC)
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
    line_thickness=4,
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
    #cv2.namedWindow('Result',flags=cv2.WINDOW_NORMAL)
    cv2.imshow('Result',raw_img)
#cv2.imshow('Stream',frame)
cv2.waitKey(0)
cv2.destroyAllWindows()
