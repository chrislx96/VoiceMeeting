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
    This part is to test backend api with java and provide code for Android application.

* [<font size=4>Application</font>](#app) <br>
    xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx


<br>


# <span id="backend"> Backend </span>
## Build backend Services
Backend is constructed of Nginx, uWsgi and Django. <br>
The easiest way to deploy them is to use Dockerfile to package them into a Docker image. 
```
docker image -t voicemeeting .
```

Specifying a port and runing the container created by this image.
```
docker container run -p 80:80 voicemeeting
```

Then we can test the APIs.



# <span id="apitest"> BackendAPITest </span>

# <span id="app"> Application </span>