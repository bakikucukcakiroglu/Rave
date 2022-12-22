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
  TextInput,
} from 'react-native';

import {useState, useEffect} from 'react';
import {Card, Title} from 'react-native-paper';

import Icon from 'react-native-vector-icons/Ionicons';

const {ConnectionModel} = NativeModules;

const Home = ({navigation, route}) => {
  const [openModalIp, setOpenModalIp] = useState('');
  const [visible, setVisible] = useState(false);
  const [rooms, setRooms] = useState([]);
  const [userName, setUserName] = useState('');

  useEffect(() => {
    const interval = setInterval(() => {
      setRooms(ConnectionModel.getServerList());
    }, 1000);

    return () => {
      clearInterval(interval);
    };
  }, []);

  useEffect(() => {
    if (route.params.loading != undefined) {
      setVisible(route.params.loading);
    }
  }, [route.params?.loading]);

  const joinRoomOnPressHandler = room => {
    navigation.setParams({
      loading: true,
    });
    setOpenModalIp('');
    ConnectionModel.connectToServer(room.address, 'test');
  };

  const createRoomOnPressHandler = () => {
    navigation.navigate('Create Room', {name: 'Jane'});
  };

  const onPressBack = () => {
    ConnectionModel.disconnectFromServer();
    navigation.setParams({
      loading: false,
    });
  };

  return visible ? (
    <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
      <ActivityIndicator color={'#2196F3'} size={'large'} />
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
              <Card key={room.address} style={styles.card}>
                <Card.Content style={styles.cardContent}>
                  <View style={{flex: 5}}>
                    <Title style={{marginTop: -5}}>{room.name} </Title>
                    <Text>Room IP: {room.address}</Text>

                    <Text>Current Member: {room.currentMembers}</Text>

                    {openModalIp == room.address && (
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
                                {'Type your user name for the room.'}
                              </Text>
                              <TextInput
                                style={{
                                  borderWidth: 1,
                                  borderColor: 'black',
                                  width: '100%',

                                  borderRadius: 100,
                                  padding: 3,
                                  paddingLeft: 10,

                                  marginBottom: 15,
                                }}
                                placeholder="User Name"
                                placeholderTextColor="#000"
                                value={userName}
                                onChangeText={setUserName}></TextInput>
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
                                    joinRoomOnPressHandler(room);
                                  }}>
                                  <Text style={styles.textStyle}>Join!</Text>
                                </Pressable>
                              </View>
                            </View>
                          </View>
                        </Modal>
                      </View>
                    )}
                  </View>
                  <View
                    style={{
                      flex: 1,
                      display: 'flex',
                      flexDirection: 'row',
                      alignItems: 'center',
                    }}>
                    <Pressable onPress={() => setOpenModalIp(room.address)}>
                      <Icon name="md-enter" size={30} color="#2196F3" />
                    </Pressable>
                  </View>
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
    height: 100,
    width: '95%',
    shadowColor: 'black',
    shadowRadius: 10,
    borderRadius: 30,
    margin: 10,
    backgroundColor: 'white',
    overflow: 'hidden',
  },
  cardContent: {
    display: 'flex',
    flexDirection: 'row',
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
  button: {
    borderRadius: 20,
    padding: 10,
    width: 65,
    marginRight: 3,
    elevation: 2,
  },
  buttonSec: {
    display: 'flex',
    flexDirection: 'column',
    borderRadius: 20,
    width: 40,
    padding: 10,
    marginRight: 3,
    elevation: 2,
    textAlign: 'center',
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
});
export default Home;
