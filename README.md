# Project Description
## Meeting recording application is an Android based software. There are four functions of our application.

* <font size=4>Basic function:</font> <br>
    Recording and saving speakers' voice.<br><br>
* <font size=4>Speaker Diarisation:</font> <br>
    the process of partitioning an audio stream with multiple people into homogeneous segments associated with each individual. <br><br>
* <font size=4>Data statistics:</font> <br>
    Using pie chart to show how long each person speaks. <br><br>
* <font size=4>Speech-to-text:</font> <br>
    Transcribing audio content into written words. 
    
    
<br>


# Components

* [<font size=4>Backend</font>](#backend) <br>
    The backend part provides three REST (Representational State Transfer) APIs, which allow users to do Http Post, Get and Delete operations. After posting wave files, the backend will parse the audio files with deep learning models and store the speaker diarisation results into database. Users can use Http Get or Delete to get or delete results. 

* [<font size=4>BackendAPITest_java</font>](#apitest) <br>
    This part is to test backend api with Java, which are used in Android application.

* [<font size=4>Application</font>](#app) <br>
    xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx


<br>


# **<span id="backend"> Backend </span>**

## **Build Backend Services**
Backend is constructed of Nginx, uWsgi and Django. <br>
The easiest way to deploy is to use Dockerfile to package them into a Docker image. 
```
docker image -t voicemeeting .
```

Specifying a port and runing the container created by this image.
```
docker container run -p 80:80 voicemeeting
```

Then we can test the APIs.

## **Rest API**

At current stage, we don't have file storage function and authentication function. Therefore, users need to send a Delete request to delete the file after getting results. It is necessary to make sure the server has free space. In addition, the server uses UUID to identify each files.

### POST
Upload wave files to server. The Post request must contain the following three arguments.
* Setting header: <br>
    * Content-Type: multipart/form-data
* Setting body: <br>
    * uuid: (a version 4 UUID)
    * file: (a *.wav file)

If the upload is successful, the server will create a new thread to analyse the file, and respond with a status code '200'. Otherwise, it will respond with '400' for bad request.

### GET
Retrive results from server. The Get request must contain one URL parameter. <br>
* uuid: (a version 4 UUID)

The response message includes a JSON string and a status code, '200' or '400'. The JSON string contains a UUID and a result. If the server is still analysing, the result will be null. Users need to resend the request after a while. The analysis process will take different time with different length of audio, so it is better to request result several time rather than wait for it.

### DELETE
Delete wave files from server. The Delete request must contain one URL parameter.
* uuid: (a version 4 UUID)


# <span id="apitest"> BackendAPITest_java </span>
Dependencies:
* group: 'org.apache.httpcomponents', name: 'httpcore', version: '4.4.13'
* group: 'org.apache.httpcomponents', name: 'httpclient', version: '4.5.11'
* group: 'org.apache.httpcomponents', name: 'httpmime', version: '4.5.11'
* group: 'org.projectlombok', name: 'lombok', version: '1.18.12'
* group: 'com.alibaba', name: 'fastjson', version: '1.2.62'
* group: 'com.squareup.okhttp3', name: 'okhttp', version: '4.4.0'

# <span id="app"> Application </span>

## Qucik Start
1. Enable Google platform Speech-to-text api with the instruction https://cloud.google.com/speech-to-text/docs
2. Download the authentication Json file 
3. paste into directory ..\VoiceMeeting\VoiceMeeting\app\src\main\res\raw 
4. Run the app


# Model Reference
* https://github.com/WeidiXie/VGG-Speaker-Recognition
* https://github.com/google/uis-rnn
* https://github.com/taylorlu/Speaker-Diarization
* https://github.com/Thumar/SpeechAPI
* https://github.com/taylorlu/Speaker-Diarization
* https://github.com/qapqap/TimelineView
* https://github.com/PhilJay/MPAndroidChart

# Contributing 
1. Fork it
2. Create your feature branch: git checkout -b -your improvement
3. Make your own change and test
4. Commit and push your changes
5. Submit a pull request