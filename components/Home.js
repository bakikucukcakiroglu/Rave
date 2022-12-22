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
                      style={[styles.button, styles.buttonClose]}>
                      <Text>{'  +  '}</Text>
                    </Pressable>
                  </View>
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
                              {'Do you want to join ' + room.name + '?'}
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
                                  joinRoomOnPressHandler(room);
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
