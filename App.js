import {React, useState, useEffect} from 'react';
import {Text, Button} from 'react-native';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import Home from './components/Home';
import Room from './components/Room';
import CreateRoom from './components/CreateRoom';

const Stack = createNativeStackNavigator();

export default function App() {
  const [status, setStatus] = useState();

  useEffect(() => {
    switch (status) {
      case 'READY':
        navigation.navigate('Home', {loading: true});

        break;

      case 'CONNECTED':
        navigation.setParams({
          query: 'someText',
        });
        navigation.navigate('Room');

        break;

      case 'SERVING':
        navigation.navigate('Room', {loading: false, role: 'slave'});
        break;
    }
  }, [status]);

  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
          name="Home"
          component={Home}
          initialParams={{itemId: 42}}
        />
        <Stack.Screen name="Room" component={Room} />
        <Stack.Screen name="Create Room" component={CreateRoom} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
