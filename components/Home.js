import React from 'react';
import {
  ScrollView,
  StyleSheet,
  Text,
  View,
  Pressable,
  Modal,
  NativeModules,
  Button,
  ActivityIndicator,
  NativeEventEmitter,
} from 'react-native';

import {useState, useEffect} from 'react';
import {Card, Title} from 'react-native-paper';

const {ConnectionModel} = NativeModules;

const Home = ({navigation, route}) => {
  const [openModalIp, setOpenModalIp] = useState('');
  const [visible, setVisible] = useState(false);
  const [rooms, setRooms] = useState([]);

  const onPressBack = () => {
    navigation.setParams({
      loading: false,
    });
  };

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

  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(NativeModules.ToastExample);
    this.eventListener = eventEmitter.addListener('EventReminder', event => {
      console.log(event.eventProperty); // "someValue"
    });
  }, []);

  useEffect(() => {
    if (route.params.loading != undefined) {
      setVisible(route.params.loading);
    }
  }, [route.params?.loading]);

  const joinRoomOnPressHandler = room => {
    //setText(ConnectionModel.thirtyOne());
    //alert(JSON.stringify(ConnectionModel.getAvailableServers()));
  };

  const createRoomOnPressHandler = () => {
    //ConnectionModel.startServer();
    navigation.navigate('Create Room', {name: 'Jane'});
  };

  return visible ? (
    <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
      <ActivityIndicator color={'#2196F3'} size={'large'} />
      <Pressable
        style={({pressed}) => ({
          opacity: pressed ? 0.5 : 1,
          padding: 10,
          backgroundColor: '#2196F3',
        })}
        onPress={onPressBack}>
        <Text style={styles.textStyle}>Back</Text>
      </Pressable>
    </View>
  ) : (
    <View>
      <Pressable
        style={({pressed}) => ({
          opacity: pressed ? 0.5 : 1,
          padding: 10,
          backgroundColor: '#2196F3',
        })}
        onPress={createRoomOnPressHandler}>
        <Text style={styles.textStyle}>Create Room</Text>
      </Pressable>
      <ScrollView>
        <View style={styles.container}>
          {rooms.map(room => {
            return (
              <Card key={room.ip} style={styles.card}>
                <Card.Content style={styles.cardContent}>
                  <Title style={{marginTop: -5}}>{room.name} </Title>
                  <View style={styles.middleCard}>
                    <Text>Room IP: {room.address}</Text>
                    <Pressable
                      onPress={() => setOpenModalIp(room.address)}
                      style={({pressed}) => ({
                        opacity: pressed ? 0.5 : 1,
                      })}>
                      {/* <AntDesign
                      name="adduser"
                      size={24}
                      color="black"
                      style={{marginRight: 15}}
                    /> */}
                      <Text>{'+'}</Text>
                    </Pressable>
                  </View>
                  <Text>Current Member: {room.currentMembers}</Text>

                  {openModalIp == room.ip && (
                    <View style={styles.centeredView}>
                      <Modal
                        animationType="slide"
                        transparent={true}
                        visible={true}
                        onRequestClose={() => {
                          //alert('Modal has been closed.');
                          setOpenModalIp('');
                        }}>
                        <View style={styles.centeredView}>
                          <View style={styles.modalView}>
                            <Text style={styles.modalText}>
                              {'Do you want to join ' + room.room_name + ' ?'}
                            </Text>
                            <View
                              style={{
                                width: '100%',
                                flexDirection: 'row',
                                justifyContent: 'flex-end',
                                alignItems: 'center',
                              }}>
                              <Pressable
                                style={[styles.button, styles.buttonClose]}
                                onPress={() => setOpenModalIp('')}>
                                <Text style={styles.textStyle}>Cancel</Text>
                              </Pressable>

                              <Pressable
                                style={[styles.button, styles.buttonClose]}
                                onPress={() => {
                                  navigation.setParams({
                                    loading: true,
                                  });
                                }}>
                                <Text style={styles.textStyle}>Yep!</Text>
                              </Pressable>
                            </View>
                          </View>
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
    </View>
  );
};

const styles = StyleSheet.create({
  card: {
    display: 'flex',
    flexDirection: 'column',
    height: 100,
    width: '95%',
    shadowColor: 'black',
    shadowRadius: 10,
    borderRadius: 30,
    margin: 10,
    backgroundColor: 'white',
  },
  cardContent: {},
  middleCard: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between',
    backgroundColor: 'white',
  },
  centeredView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    margin: 30,
  },
  textStyle: {
    color: 'white',
    fontWeight: 'bold',
    textAlign: 'center',
  },
  modalView: {
    width: '100%',
    backgroundColor: 'white',
    borderRadius: 20,

    padding: 15,
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
  modalText: {
    marginBottom: 15,
    textAlign: 'center',
    alignSelf: 'flex-start',
  },
  container: {},
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

  title: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  lottie: {
    width: 100,
    height: 100,
  },
});
export default Home;
