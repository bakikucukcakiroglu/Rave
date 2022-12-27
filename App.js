import {React, useState, useEffect} from 'react';
import {Text, Button, NativeModules, BackHandler,  Alert } from 'react-native';
import {
  NavigationContainer,
  createNavigationContainerRef,
} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import Home from './components/Home';
import Room from './components/Room';
import CreateRoom from './components/CreateRoom';

const Stack = createNativeStackNavigator();
const navigationRef = createNavigationContainerRef();
function navigate(name, params) {
  if (navigationRef.isReady()) {
    navigationRef.navigate(name, params);
  }
}

const {ConnectionModel} = NativeModules;

export default function App() {
  const [state, setState] = useState();

  useEffect(() => {
    const backAction = () => {

      switch (state) {
        case 'READY':
          
          BackHandler.exitApp();
          break;

        case 'CONNECTED':
          Alert.alert("Hold on!", "Are you sure you want to leave the room?", [
          {
            text: "Cancel",
            onPress: () => null,
            style: "cancel"
          },
          { text: "YES", onPress: () => ConnectionModel.disconnectFromServer()}
          ]);
          break;

        case 'SERVING':
          Alert.alert("Hold on!", "Are you sure you want to close the room?", [
          {
            text: "Cancel",
            onPress: () => null,
            style: "cancel"
          },
          { text: "YES", onPress: () => ConnectionModel.stopServer()}
          ]);
          break;
      }
     
      return true;
    };

    const backHandler = BackHandler.addEventListener(
      "hardwareBackPress",
      backAction
    );

    return () => backHandler.remove();
  }, [state]);

  useEffect(() => {
    const interval = setInterval(() => {
      setState(ConnectionModel.getState());
    }, 500);

    return () => {
      clearInterval(interval);
    };
  }, []);

  useEffect(() => {
    switch (state) {
      case 'READY':
        navigate('Rave', {loading: false});
        break;

      case 'CONNECTED':
        navigate('Room', {role: 'slave'});
        break;

      case 'SERVING':
        navigate('Room', {role: 'master'});
        break;
    }
  }, [state]);

  return (
    <NavigationContainer ref={navigationRef}>
      <Stack.Navigator>
        <Stack.Screen
          name="Rave"
          component={Home}
          initialParams={{itemId: 42}}
          options={{
            headerShown: false,
            //headerLeft: ()=> null
          }}
        />
        <Stack.Screen name="Room" component={Room} 
         options={{
            headerShown: false,
            //headerLeft: ()=> null
          }} />
        <Stack.Screen name="Create Room" component={CreateRoom} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
