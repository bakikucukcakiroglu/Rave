import React, { useState , useEffect} from "react";
import { View, Text, StyleSheet, Button, FlatList ,  NativeModules,} from "react-native";
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
  const [isPlaying, setIsPlaying] = useState(false);

  const handlePlayPress = () => {
    setIsPlaying(true);
    ConnectionModel.startMusic();

  };

  const handleStopPress = () => {
    setIsPlaying(false);
    ConnectionModel.stopMusic();

  };



  return (
    <View style={styles.container}>
      <View style={styles.buttonContainer}>
        <Button title="Play" onPress={handlePlayPress} />
        <Button title="Stop" onPress={handleStopPress} />
      </View>
      <FlatList
        data={mockUserData}
        style={styles.flatList}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View style={styles.cardContainer}>
            <Text style={styles.cardText}>{item.name}</Text>
            <Text style={styles.cardText}>{item.age}</Text>
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
  cardText: {
    fontSize: 18
  },
  flatList: {
    flex: 1
  }
});

export default Room;
