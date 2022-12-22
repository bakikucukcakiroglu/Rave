import {
  Text,
  Pressable,
  View,
  ScrollView,
  StyleSheet,
  NativeModules,
  VirtualizedList,
} from 'react-native';
import {Card, Title} from 'react-native-paper';

import Icon from 'react-native-vector-icons/Ionicons';

import {useState, useEffect} from 'react';

const {ConnectionModel} = NativeModules;

const Room = ({navigation, route}) => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const interval = setInterval(() => {
      let u = ConnectionModel.getUserList();

      if (u.length) {
        u[0].server = true;
      }
      setUsers(u);
      //alert(JSON.stringify(users))
    }, 1000);

    return () => {
      clearInterval(interval);
    };
  }, []);

  return (
    <View>
      <Pressable
        style={({pressed}) => ({
          opacity: pressed ? 0.5 : 1,
          padding: 10,
          backgroundColor: '#2196F3',
        })}
        onPress={() => ConnectionModel.disconnectFromServer()}>
        <Text style={styles.textStyle}>Leave Room</Text>
      </Pressable>
      <View style={styles.container}>
        <View
          style={{flex: 1, borderWidth: 5, borderColor: 'purple', zIndex: 100}}>
          {users.map((user, index) => {
            return (
              user.server && (
                <View
                  style={{
                    height: '100%',
                    borderWidth: 5,
                    borderColor: 'purple',
                    zIndex: 100,
                  }}>
                  <Card key={user.address} style={styles.card}>
                    <Card.Content style={styles.cardContent}>
                      <Title style={{marginTop: -5}}>
                        {user.server && 'Host: '}
                        {user.username}
                      </Title>
                      <View style={styles.middleCard}>
                        <Text>
                          {user.server ? 'Host IP' : 'User IP'}: {user.address}
                        </Text>
                      </View>
                    </Card.Content>
                  </Card>
                </View>
              )
            );
          })}
          <View>
            <Text>{'sdfa'}</Text>
            <Icon name="md-play-outline" size={30} color="#2196F3" />
          </View>
        </View>
        <ScrollView
          style={{
            height: '100%',
            top: '40%',
            borderWidth: 1,
            borderColor: 'red',
          }}>
          <View>
            {users.map((user, index) => {
              return (
                !user.server && (
                  <View>
                    <Card key={index} style={styles.card}>
                      <Card.Content style={styles.cardContent}>
                        <Title style={{marginTop: -5}}>
                          {user.server && 'Host: '}
                          {user.username}
                        </Title>
                        <View style={styles.middleCard}>
                          <Text>
                            {user.server ? 'Host IP' : 'User IP'}:{' '}
                            {user.address}
                          </Text>
                        </View>
                      </Card.Content>
                    </Card>
                  </View>
                )
              );
            })}
          </View>
        </ScrollView>
      </View>
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
  container: {
    height: '100%',
    borderWidth: 15,
    borderColor: 'purple',
    zIndex: 100,
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
  container: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
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

  title: {
    fontSize: 20,
    fontWeight: 'bold',
  },
});

export default Room;
