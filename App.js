import * as React from 'react';
import {Text, Button} from 'react-native';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import Home from './components/Home';
import Room from './components/Room';
import CreateRoom from './components/CreateRoom';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen name="Home" component={Home} options={{title: 'Rave'}} />
        <Stack.Screen name="Room" component={Room} />
        <Stack.Screen name="Create Room" component={CreateRoom} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
