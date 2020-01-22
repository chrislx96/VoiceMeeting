import threading
import time
import librosa
import numpy as np


class DataProcess(threading.Thread):
    def __init__(self, data_queue, args, listener):
        super().__init__()
        self.args = args
        # self.wav_output = []
        self.data_queue = data_queue
        self.listener = listener
        # self.num_chunk = int(args.RATE / args.CHUNK * args.RECORD_SECONDS)

    def run(self):
        print('=====================')
        while not self.listener.start_flag:
            pass
        time.sleep(0.1)
        count = 0
        wav_output = []
        interval_list = []
        print(self.data_queue.qsize())
        while not self.data_queue.empty():
            time.sleep(0.1)
            data = self.data_queue.get()
            interval = librosa.effects.split(data, top_db=10)
            print(interval)
            for start, end in interval:
                if start != 0 or end != len(data):
                    wav_output.extend(data[start:end])
                    interval_list.append([start + count, end + count])
            count += len(data)

            print(interval_list)
        print('+++++++++++++++++++++')


    def load_wav(self, sr):
        intervals = np.array([])
        buffer = np.array([])
        num = 0
        while num < self.num_chunk:
            data = self.data_queue.get()
            buffer = np.hstack((buffer, data))
        intervals_tmp = librosa.effects.split(buffer, top_db=20)
        count = 0


        intervals = librosa.effects.split(wav, top_db=20)
        wav_output = []
        for sliced in intervals:
            wav_output.extend(wav[sliced[0]:sliced[1]])
        return np.array(wav_output), (intervals / sr * 1000).astype(int)

    def load_data(self, win_length=400, sr=16000, hop_length=160, n_fft=512, embedding_per_second=0.5,
                  overlap_rate=0.5):
        wav, intervals = self.load_wav(sr=sr)
