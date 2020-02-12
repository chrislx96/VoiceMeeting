from rest_framework.parsers import MultiPartParser
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import status

from django.http import Http404
from .models import File
from .serializers import FileSerializer, ResultSerializer
from .apps import UploadwaveConfig
import os
import threading


# Create your views here.
class WaveFactoryView(APIView):
    parser_classes = (MultiPartParser,)

    def get_object(self, pk):
        try:
            return File.objects.get(pk=pk)
        except File.DoesNotExist:
            raise Http404

    def get(self, request, *args, **kwargs):
        file = self.get_object(request.data.get('uuid'))
        result_serializer = ResultSerializer(data={'uuid': file.uuid, 'result': file.result})

        if result_serializer.is_valid():
            return Response(result_serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(result_serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def post(self, request, *args, **kwargs):
        file_serializer = FileSerializer(data=request.data)

        if file_serializer.is_valid():
            file_serializer.save()

            file = self.get_object(request.data.get('uuid'))
            file_path = r'media/' + str(file)
            threading.Thread(target=self.process_data, args=(file_path, request)).start()

            return Response(file_serializer.data, status=status.HTTP_201_CREATED)
        else:
            return Response(file_serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, *args, **kwargs):
        file = self.get_object(request.data.get('uuid'))
        file_path = r'media/' + str(file)
        file.delete()
        os.remove(file_path)
        return Response(status=status.HTTP_204_NO_CONTENT)

    def process_data(self, path, request):
        print('==============================================')
        print('Start processing!!!')
        print('==============================================')
        result = UploadwaveConfig.predictor.predict(path)
        file = self.get_object(request.data.get('uuid'))
        file.result = result
        file.save()
        print('==============================================')
        print('Finish processing!!!')
        print('==============================================')

