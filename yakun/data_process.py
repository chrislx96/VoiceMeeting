import threading
import time
import librosa
import numpy as np


class DataProcess(threading.Thread):
    def __init__(self, data_queue, args, listener):
        super().__init__()
        self.args = args
        self.data_queue = data_queue
        self.listener = listener
        # self.num_chunk = int(args.RATE / args.CHUNK * args.RECORD_SECONDS)

        self.rate = 16000
        self.n_fft = 512
        self.win_length = 400
        self.hop_length = 160
        self.embedding_per_second = 1.2
        self.overlap_rate = 0.4

    def run(self):
        while not self.listener.start_flag:
            pass
        count = 0
        wav_output = []
        interval_list = []
        stft_idx = 0

        mag_list = []
        spec_len = self.rate / self.hop_length / self.embedding_per_second  # 83.33
        spec_hop_len = int(spec_len * (1 - self.overlap_rate))  # 50
        cur_slide = 0
        utterances_spec = []

        while True:

            # ================================
            # Extract data,
            # and split to non-silent intervals
            # ================================
            if not self.data_queue.empty():
                data = self.data_queue.get()
                interval = librosa.effects.split(data, top_db=10)
                for start, end in interval:
                    if start != 0 or end != len(data):
                        wav_output.extend(data[start:end])
                        interval_list.append([start + count, end + count])
                count += len(data)

            # ================================
            # calculate linear spectrogram
            # center = False
            # ================================
            if stft_idx + self.n_fft <= len(wav_output):
                wav = np.array(wav_output[stft_idx: stft_idx + self.n_fft])
                stft_idx += self.hop_length
                linear = librosa.stft(wav, n_fft=self.n_fft, win_length=self.win_length, hop_length=self.hop_length,
                                      center=False)
                mag, _ = librosa.magphase(linear.T)  # magnitude
                mag_list.extend(mag)  # (n, 257)
                # mag_T = mag.T
                # freq, time = mag_T.shape
                # spec_mag = mag_T

            # 0s        1s        2s                  4s                  6s
            # |-------------------|-------------------|-------------------|
            # |-------------------|
            #           |-------------------|
            #                     |-------------------|
            #                               |-------------------|
            if len(mag_list) - cur_slide >= 83:
                spec_mag = np.array(mag_list[cur_slide: cur_slide + 83]).T
                # preprocessing, subtract mean, divided by time-wise var
                mu = np.mean(spec_mag, 0, keepdims=True)
                std = np.std(spec_mag, 0, keepdims=True)
                spec_mag = (spec_mag - mu) / (std + 1e-5)
                utterances_spec.append(spec_mag)
                cur_slide += spec_hop_len

            # ================================
            # interval slices to maptable
            # ================================

            print(len(utterances_spec))
            print(interval_list)
