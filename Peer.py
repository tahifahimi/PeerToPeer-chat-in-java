import socket
import threading
import tkinter as tk

udpPort = 1080
myTcpPort = 4400


def startTCP():
    print("ffff")
    # s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # s.bind(('localhost', myTcpPort))
    # s.listen(1)
    # conn, addr = s.accept()
    # while 1:
    #     data = conn.recv(1024)
    #     print(data)
    #     if not data:
    #         break
    # conn.close()

def broadcasting():
    s_br = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s_br.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    # while True:
    #     s_br.sendto('new packet is coming'.encode(), ('255.255.255.255',1080))
    #     print("each broadcasting are : ",1080)
    #     time.sleep(1)
    # s_br.close()
    s_br.sendto(b'i am ready to chat ... ', ('255.255.255.255', udpPort))
    print("each broadcasting are : ", udpPort)

    #ask to chat in tcp from client.......
    #...

    #start sending tcp port to the destination
    messagePort = b'my tcp port is : 4400'
    s_br.sendto(messagePort, ('192.168.56.1', udpPort))
    s_br.close()
    startTCP()

def reciving():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    s.bind(("", udpPort))
    while True:
        d, add = s.recvfrom(1024)
        data = d.decode('utf-8')
        print(add, " ---> ", data)
        print("&&&&&&&&&&&&&&777")
        mes = data.split()
        print(mes[-1])
        break

if __name__ == '__main__':
    window = tk.Tk()
    window.title('chat window')
    print("$$$4")
    but = tk.Button(window,text="start tcp chat",width=100,height=100,command=startTCP())
    but.grid(row=1,column=0)

    tk.Label(window,text="enter the port of friend: ",width=50,height=50).grid(row=0,column=0)
    ePort = tk.Entry(window).grid(row=0,column=1)


    print("rrrr")
    window.mainloop()
    print("6666")
    #br = threading.Thread(target=broadcasting())
    #rec = threading.Thread(target= reciving())

    #br.start()
    #rec.start()
