import socket
import threading

port = 1080
myTcpPort = 4400


def startTCP():
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('localhost', myTcpPort))
    mes = 'lets have private chat ...'.encode()
    s.sendto(mes, ('192.168.56.1', 1080))
    s.listen(1)
    conn, addr = s.accept()
    while 1:
        data = conn.recv(1024)
        print(data)
        if not data:
            break
        conn.sendall(data)
    conn.close()


def sending():
    print("send ready message to  : ")
    # hisIp = input()
    # hisPort =input(int)
    startTCP()


def broadcasting():
    s_br = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s_br.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    # while True:
    #     s_br.sendto('new packet is coming'.encode(), ('255.255.255.255',1080))
    #     print("each broadcasting are : ",1080)
    #     time.sleep(1)
    # s_br.close()
    s_br.sendto('i am ready to chat ... '.encode(), ('255.255.255.255', port))
    print("each broadcasting are : ", port)
    # time.sleep(1)
    # start sending port and ip


def reciving():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    s.bind(("", port))
    while True:
        data, add = s.recvfrom(1024)
        print(add, " ---> ", data)


if __name__ == '__main__':
    br = threading.Thread(target=broadcasting())
    # rec = threading.Thread(target= reciving())

    br.start()
    # rec.start()
