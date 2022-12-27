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
  FlatList
} from 'react-native';

import {useState, useEffect} from 'react';
import {Card, Title} from 'react-native-paper';

import Icon from 'react-native-vector-icons/Ionicons';
import { color } from '@rneui/base';

const {ConnectionModel} = NativeModules;

const Home = ({navigation, route}) => {
  const [openModalIp, setOpenModalIp] = useState('');
  const [visible, setVisible] = useState(false);
  const [rooms, setRooms] = useState([]);
  const [userName, setUserName] = useState('');
  const [roomName, setRoomName] = useState('');
  const [isCreateRoomModalOpen, setIsCreateRoomModalOpen] = useState(false);

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

  const joinRoomOnPressHandler =()=> {
    navigation.setParams({
      loading: true,
    });
    ConnectionModel.connectToServer(openModalIp, userName);
    setOpenModalIp('');
    setUserName('');

  };

  const createRoomOnPressHandler = () => {
    setIsCreateRoomModalOpen(true);
  };


   const handleSubmitCreateRoom = () => {
     setUserName(''); 
     setRoomName(''); 
     setIsCreateRoomModalOpen(false);
    ConnectionModel.startServer(roomName, userName);
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
    <View style={styles.container}>
      <View style={{ height:"7%", display:"flex", flexDirection:"row", alignItems:"center", justifyContent:"flex-start", paddingLeft:15}}>
    
          <Text style={ { color: 'black',fontWeight: 'bold', textAlign: 'center', fontSize:20}}>Rave</Text>
     
      </View>
      
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
      </View>
      <FlatList
        data={rooms}
        style={styles.flatList}
        keyExtractor={(item) => item.address ? item.address : 0}
        renderItem={({ item }) => (
          <View style={styles.cardContainer}>
            
            

            <View style={{flex:1,display:"flex", flexDirection:"column", justifyContent:"center"}}>
              <Text style={styles.cardTextRoomName}><Icon name="ios-people-circle" size={30} color="black" />{" "} {item.name}</Text>
              <Text style={styles.cardText}>{" IP: "}{item.address}</Text>
            </View>
            <Pressable onPress={() => setOpenModalIp(item.address)}>
              <Icon name="md-enter" size={35} color="#2196F3" />
            </Pressable>
        
          </View>
        )}
      />
  
      <Modal
        animationType="slide"
        transparent={true}
        visible={openModalIp!=''}
        onRequestClose={() => {
          setOpenModalIp('');
        }}>
        <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
          <View style={{width:"100%", backgroundColor: 'white', padding: 20, borderRadius:10, margin:10, shadowRadius:10, shadowColor:"black", elevation:10}}>
            <Text style={styles.modalText}>
              {'Type your user name to join the room.'}
            </Text>
            <TextInput
              style={{
                borderWidth: 1,
                borderColor: 'darkgray',
                width: '100%',

                borderRadius: 100,
                padding: 3,
                paddingLeft: 10,

                marginBottom: 15,
              }}
              placeholder="User Name"
              placeholderTextColor="darkgray"
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
                onPress={() => {setOpenModalIp(''); setUserName('');}}>
                <Text style={styles.textStyle}>Cancel</Text>
              </Pressable>

              <Pressable
                disabled= {!userName.length}
                style={userName.length ? { backgroundColor: '#2196F3', ...styles.button}: { backgroundColor: 'darkgray',  ...styles.button}}
                onPress={
                  ()=> {joinRoomOnPressHandler();}
                }>
                <Text style={styles.textStyle}>Join!</Text>
              </Pressable>
            </View>
          </View>
        </View>
      </Modal>
      <Modal
        animationType="slide"
        transparent={true}
        visible={isCreateRoomModalOpen}
        onRequestClose={() => {
          setIsCreateRoomModalOpen(false);
        }}>
        <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
          <View style={{width:"100%", backgroundColor: 'white', padding: 20, borderRadius:10, margin:10, shadowRadius:10, shadowColor:"black", elevation:10}}>
            
            <Text style={{color:"black"}}> User Name</Text>
            <TextInput
              style={{
                borderWidth: 1,
                borderColor: 'darkgray',
                borderRadius: 100,
                padding: 5,
                paddingLeft: 15,
                marginBottom: 20,
              }}
              placeholder="User Name"
              placeholderTextColor="darkgray"
              value={userName}
              onChangeText={setUserName}></TextInput>
            <Text style={{color:"black"}}>Room Name</Text>
            <TextInput
              style={{
                borderWidth: 1,
                borderColor: 'darkgray',
                borderRadius: 100,
                padding: 5,
                paddingLeft: 15,
                marginBottom: 20,
              }}
              placeholder="Room Name"
              placeholderTextColor="darkgray"
              value={roomName}
              onChangeText={setRoomName}>
            </TextInput>
            <View
              style={{
                width: '100%',
                flexDirection: 'row',
                justifyContent: 'flex-end',
                alignItems: 'center',
              }}>
              <Pressable
                style={[styles.button, styles.buttonClose]}
                onPress={() => {setUserName(''); setRoomName(''); setIsCreateRoomModalOpen(false);}}>
                <Text style={styles.textStyle}>Cancel</Text>
              </Pressable>

              <Pressable
                disabled= {!userName.length && !roomName.length}
                style={userName.length ? { backgroundColor: '#2196F3', ...styles.button}: { backgroundColor: 'darkgray',  ...styles.button}}
                onPress={
                  ()=> { handleSubmitCreateRoom()}
                }>
                <Text style={styles.textStyle}>Create!</Text>
              </Pressable>
            </View>
          </View>
        </View>
      </Modal>
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
    fontWeight:"bold"
  },
  button: {
    borderRadius: 20,
    padding: 10,
    width: 70,
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

  buttonClose: {
    backgroundColor: '#2196F3',
  },

  title: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  flatList: {
    flex: 1
  },
  cardContainer: {
    padding: 16,
    margin: 8,
    backgroundColor: "#fff",
    borderRadius: 8,
    borderColor: "green",
    display:"flex",
    flexDirection:"row",
    alignItems:"center",
    justifyContent:"space-between",
    elevation:3

  },
  container: {
    flex: 1,
    borderColor: "blue",

  },
   cardTextRoomName: {
    fontSize: 20,
    textAlignVertical:"center"

  },
});
export default Home;
