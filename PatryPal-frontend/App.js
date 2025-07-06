import { NavigationContainer } from '@react-navigation/native';
import React, { useEffect, useReducer} from "react";
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from './components/HomeScreen'
import AddProductScreen from './components/AddProductScreen';
import inventoryListScreen from './components/InventoryListScreen';
import UserProfileScreen from './components/UserProfileScreen';
import ScanProductScreen from './components/ScanProductScreen'; 
import ViewTrendsScreen from './components/ViewTrendsScreen';
import Signin from './components/SigninScreen';
import Signup from './components/SignupScreen';
import AdminHomeScreen from './components/AdminHomeScreen';
import NfcManager from 'react-native-nfc-manager';

const Stack = createNativeStackNavigator();

export default function App() {
  
useEffect(() => {
  NfcManager.start()
    .then(() => console.log("nfc module initialized"))
    .catch((e) => console.warn("NFC init failed", e));
}, []);

  return (
    <>
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen name="Signin" component={Signin} />
        <Stack.Screen name="Signup" component={Signup} />
        <Stack.Screen name="AdminHome" component={AdminHomeScreen}/>
        <Stack.Screen name="Home" component={HomeScreen} />
        <Stack.Screen name="AddProduct" component={AddProductScreen} />
        <Stack.Screen name="InventoryList" component={inventoryListScreen}/>
        <Stack.Screen name="UserProfile" component={UserProfileScreen}/>
        <Stack.Screen name="ScanProduct" component={ScanProductScreen} />
        <Stack.Screen name="ViewTrends" component={ViewTrendsScreen} />
      </Stack.Navigator>
    </NavigationContainer>
    </>
  );
}


