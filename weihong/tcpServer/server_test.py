from threading import Thread
from socket import *


def recv_data(new_socket, client_info):
    print("客户端{}已经连接".format(client_info))

    receive = new_socket.recv(1024).decode("UTF-8")
    print(receive[2:])

    # 接受数据
    if receive[2:] == "start":

        # file name

        file_name = new_socket.recv(1024).decode("UTF-8")
        file_name = file_name[2:]
        print("file_name:  " + file_name)

        # file length
        file_length = new_socket.recv(1024)
        print("file_length:  " + str(file_length))

        file_size = int.from_bytes(file_length, byteorder='big', signed=False)
        print("file_size:  "+str(file_size))

        # while raw_data:
        #     print(f"收到来自{client_info}的数据：{raw_data}")
        #     raw_data = new_socket.recv(1024)
        # new_socket.close()

        # 下面是循环接收文件内容的部分
        num = file_size / 1024
        if num != int(num):
            num = int(num) + 1
        else:
            num = int(num)

        with open('myfile.wav', mode='bx') as f:
            for i in range(num):
                content = new_socket.recv(1024)
                f.write(content)





def send_data(new_socket):
    while True:
        msg = input('>>')
        msg = msg.encode('UTF-8')
        new_socket.send(len(msg).to_bytes(2, byteorder='big'))
        new_socket.send(msg)

def main():
    # 实例化socket对象
    socket_server = socket(AF_INET, SOCK_STREAM)
    # 设置端口复用
    socket_server.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
    # 绑定IP地址和端口
    socket_server.bind(("10.13.246.153", 7788))
    # 改主动为被动，监听客户端
    socket_server.listen(5)
    while True:
        # 等待连接
        new_socket, client_info = socket_server.accept()

        # receive data thread
        tr = Thread(target=recv_data, args=(new_socket, client_info))
        tr.start()

        # send data thread
        ts = Thread(target=send_data, args=(new_socket,))
        ts.start()
        # 多线程共享一片内存区域，所以这里不用关闭
        # new_socket.close()


if __name__ == '__main__':
    main()