import React from 'react';
import {
  ScrollView,
  StyleSheet,
  Text,
  View,
  Pressable,
  Modal,
  NativeModules,
} from 'react-native';

import {useState} from 'react';
import {Card, Title} from 'react-native-paper';

const {ConnectionModel} = NativeModules;

export default function App() {
  const [isStreaming, setIsStreaming] = useState(false);
  const [openModalIp, setOpenModalIp] = useState('');
  const [text, setText] = useState('+');

  const data = [
    {ip: '192.168.1.1', room_name: 'Room 1', current_members: 2},
    {ip: '192.168.1.2', room_name: 'Room 2', current_members: 2},
    {ip: '192.168.1.3', room_name: 'Room 3', current_members: 3},
    {ip: '192.168.1.4', room_name: 'Room 4', current_members: 4},
    {ip: '192.168.1.5', room_name: 'Room 5', current_members: 4},
    {ip: '192.168.1.6', room_name: 'Room 6', current_members: 4},
    {ip: '192.168.1.7', room_name: 'Room 7', current_members: 4},
    {ip: '192.168.1.8', room_name: 'Room 8', current_members: 4},
    {ip: '192.168.1.9', room_name: 'Room 9', current_members: 4},
    {ip: '192.168.1.10', room_name: 'Room 10', current_members: 4},
    {ip: '192.168.1.11', room_name: 'Room 11', current_members: 4},
  ];

  const handlePress = room => {
    setOpenModalIp(room.ip);
    setText(ConnectionModel.thirtyOne());
    alert(JSON.stringify(ConnectionModel.getAvailableServers()));
  };

  return !isStreaming ? (
    <ScrollView>
      <View style={styles.container}>
        {data.map(room => {
          return (
            <Card key={room.ip} style={styles.card}>
              <Card.Content style={styles.cardContent}>
                <Title style={{marginTop: -5}}>{room.room_name} </Title>

                <View
                  style={{
                    display: 'flex',
                    flexDirection: 'row',
                    justifyContent: 'space-between',
                    backgroundColor: 'white',
                  }}>
                  <Text>Room IP: {room.ip}</Text>
                  <Pressable
                    onPress={() => handlePress(room)}
                    style={({pressed}) => ({
                      opacity: pressed ? 0.5 : 1,
                    })}>
                    {/* <AntDesign
                      name="adduser"
                      size={24}
                      color="black"
                      style={{marginRight: 15}}
                    /> */}
                    <Text>{text}</Text>
                  </Pressable>
                </View>
                <Text>Current Member: {room.current_members}</Text>

                {openModalIp == room.ip && (
                  <View style={styles.centeredView}>
                    <Modal
                      animationType="slide"
                      transparent={true}
                      visible={true}
                      onRequestClose={() => {
                        alert('Modal has been closed.');
                        setOpenModalIp('');
                      }}>
                      <View style={styles.centeredView}>
                        <Text style={styles.modalText}>{room.room_name}</Text>
                        <Pressable
                          style={[styles.button, styles.buttonClose]}
                          onPress={() => setOpenModalIp('')}>
                          <Text style={styles.textStyle}>Hide Modal</Text>
                        </Pressable>
                      </View>
                    </Modal>
                  </View>
                )}
              </Card.Content>
            </Card>
          );
        })}
      </View>
    </ScrollView>
  ) : (
    <View style={styles.container}>
      <Text style={styles.title}>Köle</Text>
      <View
        style={styles.separator}
        lightColor="#eee"
        darkColor="rgba(255,255,255,0.1)"
      />
      <EditScreenInfo path="/screens/TabTwoScreen.tsx" />
    </View>
  );
}

const styles = StyleSheet.create({
  centeredView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 22,
  },
  modalView: {
    margin: 20,
    backgroundColor: 'white',
    borderRadius: 20,
    padding: 35,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  button: {
    borderRadius: 20,
    padding: 10,
    elevation: 2,
  },
  buttonOpen: {
    backgroundColor: '#F194FF',
  },
  buttonClose: {
    backgroundColor: '#2196F3',
  },
  textStyle: {
    color: 'white',
    fontWeight: 'bold',
    textAlign: 'center',
  },
  modalText: {
    marginBottom: 15,
    textAlign: 'center',
  },
  card: {
    display: 'flex',
    flexDirection: 'column',
    height: 100,
    width: '100%',
    shadowColor: 'black',
    shadowRadius: 10,
    borderRadius: 30,
    margin: 5,
    backgroundColor: '#E9E3C4',

    backgroundColor: 'white',
    // borderColor: "red",
    // borderWidth: 1
  },
  cardTitle: {},
  cardContent: {
    // borderColor: "red",
    // borderWidth: 1
  },
  container: {
    // flex: 1,
    // flexDirection: "column",
    // alignItems: 'center',
    // justifyContent: 'center',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  separator: {
    marginVertical: 30,
    height: 1,
    width: '80%',
  },
});
