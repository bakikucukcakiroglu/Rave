import {
  StyleSheet,
  Image,
  Button,
  TouchableOpacity,
  TextInput,
  Text,
  View,
  Pressable,
  NativeModules,
} from 'react-native';
import {useState} from 'react';
import WheelPicker from 'react-native-wheely';

const {ConnectionModel} = NativeModules;

const CreateRoom = ({navigation}) => {
  const [email, setEmail] = useState('');
  const [userName, setUserName] = useState('');
  const [roomName, setRoomName] = useState('');
  const [roomCapacity, setRoomCapacity] = useState(0);

  const [selectedIndex, setSelectedIndex] = useState(0);

  const handleSubmitCreateRoom = () => {
    ConnectionModel.startServer(roomName, userName);
  };

  return (
    <View style={styles.container}>
      <View style={styles.inputGroup}>
        <Text style={styles.inputLabel}> User Name</Text>
        <TextInput
          style={{
            borderWidth: 1,
            borderColor: 'black',
            marginLeft: 15,
            marginRight: 15,
            borderRadius: 100,
            padding: 5,
            paddingLeft: 15,
            marginBottom: 20,
          }}
          placeholder="User Name"
          placeholderTextColor="#000"
          value={userName}
          onChangeText={setUserName}></TextInput>
        <Text style={styles.inputLabel}>Room Name</Text>
        <TextInput
          style={{
            borderWidth: 1,
            borderColor: 'black',
            marginLeft: 15,
            marginRight: 15,
            borderRadius: 100,
            padding: 5,
            paddingLeft: 15,
            marginBottom: 20,
          }}
          placeholder="Room Name"
          placeholderTextColor="#000"
          value={roomName}
          onChangeText={setRoomName}></TextInput>
        <Text style={styles.inputLabel}>Room Capacity</Text>
        <WheelPicker
          visibleRest={1}
          selectedIndex={roomCapacity}
          itemHeight={40}
          options={[2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]}
          onChange={index => setSelectedIndex(index)}
          containerStyle={{
            borderColor: 'black',
            borderWidth: 1,
            marginLeft: 15,
            marginRight: 15,
            borderRadius: 10,
            marginBottom: 20,
          }}
        />
      </View>
      <Pressable
        style={({pressed}) => (pressed ? styles.buttonPressed : styles.button)}
        onPress={handleSubmitCreateRoom}>
        <Text style={styles.textStyle}>Let's GO</Text>
      </Pressable>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-start',
    borderWidth: 0,
    borderColor: 'white',
    backgroundColor: 'white',
  },
  userProfileImage: {
    color: 'black',
    width: 150,
    height: 150,
    bottom: -75,
    zIndex: 100,
    position: 'absolute',
    alignSelf: 'center',
    borderWidth: 0,
    borderColor: 'red',
  },
  userHeader: {
    flex: 1,
    borderWidth: 0,
    borderColor: '#06D7E4',
    backgroundColor: '#0B1B54',
    zIndex: 100,
  },
  inputGroup: {
    backgroundColor: 'white',
    marginTop: 30,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    borderWidth: 1,
    borderColor: 'red',
    textAlign: 'center',
  },
  button: {
    opacity: 1,
    width: '92%',
    marginLeft: 16,
    marginRight: 15,
    borderRadius: 100,
    padding: 10,
    backgroundColor: '#2196F3',
    position: 'absolute',
    bottom: 30,
  },
  buttonPressed: {
    opacity: 0.5,
    width: '92%',
    marginLeft: 16,
    marginRight: 15,
    borderRadius: 100,
    padding: 10,
    backgroundColor: '#2196F3',
    position: 'absolute',
    bottom: 30,
  },
  textStyle: {
    color: 'white',
    fontWeight: 'bold',
    textAlign: 'center',
  },
  inputLabel: {
    padding: 5,
    marginLeft: 10,
  },
});

export default CreateRoom;
