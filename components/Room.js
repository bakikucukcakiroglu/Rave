import React, { useState , useEffect} from "react";
import { View, Text, StyleSheet, Button, FlatList ,  NativeModules,Pressable} from "react-native";
import Icon from "react-native-vector-icons/Ionicons";

const {ConnectionModel} = NativeModules;


const mockUserData = [
  {
    id: "1",
    name: "John Smith",
    age: 32
  },
  {
    id: "2",
    name: "Jane Doe",
    age: 28
  },
  {
    id: "3",
    name: "Bob Johnson",
    age: 45
  },
  {
    id: "2",
    name: "Jane Doe",
    age: 28
  },
  {
    id: "3",
    name: "Bob Johnson",
    age: 45
  },
   {
    id: "2",
    name: "Jane Doe",
    age: 28
  },
  {
    id: "3",
    name: "Bob Johnson",
    age: 45
  }
];

const Room = () => {
  const [users, setUsers] = useState([]);
  const [musicState, setMusicState] = useState("STOPPED");
  const [isWaiting, setIsWaiting]  = useState(false);



  useEffect(() => {
    const interval = setInterval(() => {

      
      setUsers(ConnectionModel.getUserList());
    }, 1000);

    return () => {
      clearInterval(interval);
    };
  }, []);


  useEffect(() => {
    const interval = setInterval(() => {

      let state = ConnectionModel.getMusicState();

      if(state != "WAIT"){

        setMusicState(ConnectionModel.getMusicState());
        setIsWaiting(false);
      }else{

        setIsWaiting(true);
      }
    }, 150);

    return () => {
      clearInterval(interval);
    };
  }, []);

  return (
    <View style={styles.container}>
      {musicState != "PLAYING" ? (<View style={styles.buttonContainer}>
        <Pressable
          disabled={isWaiting}
          style={{
            width: "100%",
            height: "100%",
            backgroundColor: !isWaiting  ? "#2196F3": "darkgray",
            display: "flex",
            justifyContent: "center",
            alignItems: "center"
          }}
          onPress={() =>  ConnectionModel.startMusic()}
        >
          <Icon name="play" size={50} color="white" />
        </Pressable>
      </View>)
       :(<View style={styles.buttonContainer}>
         <Pressable
          disabled={isWaiting}
          style={{
            flex:1,
            height: "100%",
            backgroundColor: !isWaiting ? "gray" : "darkgray",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            borderRightWidth :1,
            borderColor:"white",
          }}
          onPress={() => ConnectionModel.stopMusic()}
        >
          <Icon name="stop" size={50} color="white" />
        </Pressable>
       
          <Pressable
            disabled={isWaiting}
            style={{
              flex:1,
              height: "100%",
              backgroundColor:!isWaiting ? "gray" : "darkgray",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
      
         
            //borderStyle:"solid"
          }}
          onPress={() =>  ConnectionModel.pauseMusic()}
        >
          <Icon name="pause" size={50} color="white" />
        </Pressable>
      </View>)}
       
      {users.find((user) => !user.address) &&
      <View style={styles.cardContainerHost}>
        <Text style={styles.cardText}>
            <Icon name="home" size={20} color="black" /> {"  "}{users.find((user) => !user.address).username}</Text>
      </View>}
      <FlatList
        data={users.filter((user)=> user.address)}
        style={styles.flatList}
        // keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View style={styles.cardContainer}>
            <Text style={styles.cardText}>{item.username}</Text>
            <Text style={styles.cardText}>{item.address}</Text>
          </View>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    borderColor: "blue",

  },
  buttonContainer: {
    height: "30%",
    flexDirection: "row",
    justifyContent: "space-around",
    alignItems: "center",
    borderColor: "red",
  
  },
  cardContainer: {
    flex: 1,
    padding: 16,
    margin: 8,
    backgroundColor: "#fff",
    borderRadius: 8,
    borderColor: "green",

  },
   cardContainerHost: {
    height:"10%",
    padding: 16,
    margin: 8,
    backgroundColor: "#fff",
    borderRadius: 1,
    borderColor: "blue",
    border:"solid",
    display:"flex",
    flexDirection:"row",
    justifyContent:"space-between",
    alignItems:"center"

  },
  cardText: {
    fontSize: 18,

  },
  flatList: {
    flex: 1
  }
});

export default Room;
