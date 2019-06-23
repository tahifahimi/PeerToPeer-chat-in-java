import socket
import threading
import time
import random
import tkinter

port = 1080
myTcpPort = 4400

def tcpServer():
    t = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    t.bind(('192.168.56.1', myTcpPort))
    print("waiting for the client... on port and ip of ",myTcpPort )
    t.listen()
    connection, address = t.accept()
    print(address, " is connected ")
    connection.sendall("Thank you for connecting")
    while True:
        data = connection.recv(1024).decode("utf-8")
        if data == "close" or data == "\n":
            break
        print(address, "---> ",data)
        print("$")
        mes = input().encode("utf-8")
        connection.send(mes)

    t.close()

def startTCP(hisip , hisport):
    print("in start tcp")
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # s.bind(('localhost', myTcpPort))
    s.connect((hisip, hisport))
    print("in tcp port")
    mes = 'lets have private chat ...'.encode()
    s.sendto(mes, (hisip, hisport))
    while True:
        data = s.recv(1024).decode("utf-8")
        if data=="close":
            break
        print(hisip ,"  ",hisport,"---> ",data)
        mes = input().encode("utf-8")
        s.sendto(mes,(hisip,hisport))

    s.close()

def sending():
    print("send ready message to  : ")
    # hisIp = input()
    # hisPort =input()
    hisIp = "192.168.56.1"
    hisPort = "4400"
    print("the ip and port enterde :" ,type(hisIp) ,type(int(hisPort,10)))
    startTCP(hisIp,int(hisPort,10))


def broadcasting():
    s_br = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s_br.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    s_br.sendto('hello'.encode(), ('255.255.255.255', port))
    print("each broadcasting are : ", port)
    tcpServer()
    # while True:
    #     s_br.sendto('new packet is coming'.encode(), ('255.255.255.255',1080))
    #     print("each broadcasting are : ",1080)
    #     time.sleep(1)
    # s_br.close()


def sendPort(add):
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    myTcpPort = random.randint(1234,66100)
    print("my tcp port is : ",myTcpPort)
    mes = "lets chat on "+str(myTcpPort)
    sock.sendto(mes.encode(), add)
    print("tcp port is tranmitted")


def reciving():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    s.bind(("", port))
    print(time.thread_time())
    timeout = time.time() + 10.11; #stay for a minute in the loop
    while True:
        if time.time() >= timeout:
            break
        # s.settimeout(10)
        print("in reciving ")
        d, add = s.recvfrom(1024)
        data = d.decode("utf-8")
        print("the sock name is ", add)
        print(add, " --->", data)
        if data == "hello":
            print("in sending")
            sendPort(add)
            break
    s.close()
    print("close the reciving...")
    sending()

if __name__ == '__main__':
    # br = threading.Thread(target=broadcasting())
    rec = threading.Thread(target= reciving())

    # br.start()
    rec.start()