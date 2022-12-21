import {Text, Pressable, View, ScrollView, StyleSheet} from 'react-native';
import {Card, Title} from 'react-native-paper';

import {useState} from 'react';

const Room = ({navigation, route}) => {
  const [users, setUsers] = useState([]);
  const data = [
    {ip: '192.168.1.1', user_name: 'User 1', current_members: 2},
    {ip: '192.168.1.2', user_name: 'User 2', current_members: 2},
    {ip: '192.168.1.3', user_name: 'User 3', current_members: 3},
  ];

  return (
    <View>
      <Pressable
        style={({pressed}) => ({
          opacity: pressed ? 0.5 : 1,
          padding: 10,
          backgroundColor: '#2196F3',
        })}
        //onPress={}
      >
        <Text style={styles.textStyle}>Leave Room</Text>
      </Pressable>
      <ScrollView>
        <View style={styles.container}>
          {data.map(user => {
            return (
              <Card key={user.ip} style={styles.card}>
                <Card.Content style={styles.cardContent}>
                  <Title style={{marginTop: -5}}>{user.user_name} </Title>
                  <View style={styles.middleCard}>
                    <Text>User IP: {user.ip}</Text>
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
});

export default Room;
