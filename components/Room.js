import React, { useState , useEffect} from "react";
import { View, Text, StyleSheet, Button, FlatList ,  NativeModules,Pressable, Alert} from "react-native";
import Icon from "react-native-vector-icons/Ionicons";

const {ConnectionModel} = NativeModules;

const Room = ({navigation, route}) => {
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

  function handleLeaveRoom(){

    if(route.params.role == 'master'){

       Alert.alert("Hold on!", "Are you sure you want to close the room?", [
          {
            text: "Cancel",
            onPress: () => null,
            style: "cancel"
          },
          { text: "YES", onPress: () => ConnectionModel.stopServer()}
      ]);
       

      
    }else{


       Alert.alert("Hold on!", "Are you sure you want to leave the room?", [
          {
            text: "Cancel",
            onPress: () => null,
            style: "cancel"
          },
          { text: "YES", onPress: () => ConnectionModel.disconnectFromServer()}
      ]);
       
    }
  }

  const handleSubmitCreateRoom = () => {
    ConnectionModel.startServer(roomName, userName);
  };

  return (
    <View style={styles.container}>
      <View style={{ height:"7%", display:"flex", flexDirection:"row", alignItems:"center", justifyContent:"flex-start", paddingLeft:15}}>
      <Text style={ { color: 'black',fontWeight: 'bold', textAlign: 'center', fontSize:20}}>Room</Text>
      </View>
      <View>
        <Pressable
          style={({pressed}) => ({
            opacity: pressed ? 0.5 : 1,
            padding: 10,
            backgroundColor: '#2196F3',
            borderBottomWidth:1,
            borderColor:"white"
          })}
          onPress={() =>  handleLeaveRoom()}

          >

          <Text style={styles.textStyle}>Leave Room</Text>
        </Pressable>
      </View>
     
      {route.params.role=='master' ? (musicState != "PLAYING" ? (<View style={styles.buttonContainer}>
        <Pressable
          disabled={isWaiting}
          style={({pressed}) => ({
            opacity: pressed ? 0.5 : 1,
            width: "100%",
            height: "100%",
            backgroundColor: !isWaiting  ? "#2196F3": "darkgray",
            display: "flex",
            justifyContent: "center",
            alignItems: "center"
          })}
          onPress={() =>  ConnectionModel.startMusic()}
        >
          <Icon name="play" size={50} color="white" />
        </Pressable>
      </View>)
       :(<View style={styles.buttonContainer}>
         <Pressable
          disabled={isWaiting}
          style={({pressed}) => ({
            opacity: pressed ? 0.5 : 1,
            flex:1,
            height: "100%",
            backgroundColor: !isWaiting ? "gray" : "darkgray",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            borderRightWidth :1,
            borderColor:"white",
          })}
          onPress={() => ConnectionModel.stopMusic()}
        >
          <Icon name="stop" size={50} color="white" />
        </Pressable>
          <Pressable
            disabled={isWaiting}
            style={({pressed}) => ({
              opacity: pressed ? 0.5 : 1,
              flex:1,
              height: "100%",
              backgroundColor:!isWaiting ? "gray" : "darkgray",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
          })}
          onPress={() =>  ConnectionModel.pauseMusic()}
        >
          <Icon name="pause" size={50} color="white" />
        </Pressable>
      </View>)) : (

        musicState=="PLAYING" ? 

        (<View style={styles.buttonContainer}>
         <Pressable
          disabled={isWaiting}
          style={({pressed}) => ({
            opacity: pressed ? 0.5 : 1,
            width: "100%",
            height: "100%",
            backgroundColor: !isWaiting  ? "#2196F3": "darkgray",
            display: "flex",
            justifyContent: "center",
            alignItems: "center"
          })}
          onPress={() =>  ConnectionModel.startMusic()}
        >
          <Icon name="play" size={50} color="white" />
        </Pressable>
        </View> ) : (

          musicState=="STOPPED" ? (


            <Pressable
              disabled={isWaiting}
              style={({pressed}) => ({
                opacity: pressed ? 0.5 : 1,
                flex:1,
                height: "100%",
                backgroundColor: !isWaiting ? "gray" : "darkgray",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                borderRightWidth :1,
                borderColor:"white",
              })}
              onPress={() => ConnectionModel.stopMusic()}
            >
              <Icon name="stop" size={50} color="white" />
            </Pressable>
          ) : (
              <Pressable
                disabled={isWaiting}
                style={({pressed}) => ({
                  opacity: pressed ? 0.5 : 1,
                  flex:1,
                  height: "100%",
                  backgroundColor:!isWaiting ? "gray" : "darkgray",
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
              })}
              onPress={() =>  ConnectionModel.pauseMusic()}
            >
              <Icon name="pause" size={50} color="white" />
            </Pressable>
          )
        )
        )
      }
      {users.find((user) => !user.address) &&
      <View style={styles.cardContainerHost}>
        <Text style={{fontWeight:"bold",  fontSize:22, color:"black"}}>
            <Icon name="home" size={22} color="black" /> {"  "}{users.find((user) => !user.address).username}</Text>
      </View>}
      <FlatList
        data={users.filter((user)=> user.address)}
        style={styles.flatList}
        // keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View style={styles.cardContainer}>
            <Text style={{fontWeight:"bold",  fontSize:18, color:"black"}}><Icon name="ios-person" size={18} color="black" />{"  "}{item.username}</Text>
            <Text style={{ fontSize:14, color:"gray"}}><Icon name="ios-logo-rss" size={14} color="gray" /> {" "}{item.address}</Text>
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

  flatList: {
    flex: 1
  },
    textStyle: {
    color: 'white',
    fontWeight: 'bold',
    textAlign: 'center',
  },
});

export default Room;
