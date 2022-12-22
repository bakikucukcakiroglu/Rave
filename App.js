import {React, useState, useEffect} from 'react';
import {Text, Button, NativeModules} from 'react-native';
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
        />
        <Stack.Screen name="Room" component={Room} />
        <Stack.Screen name="Create Room" component={CreateRoom} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
