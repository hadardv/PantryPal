import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Alert, Button } from 'react-native';
import NfcManager, { NfcTech, Ndef } from 'react-native-nfc-manager';
import axios from 'axios';


const ScanProductScreen = ({ route,navigation }) => {
  const { user } = route.params;
  const [productData, setProductData] = useState(null);

  const USER_BASE = "http://10.100.102.4:8084/ambient-intelligence/users";

  const BASE_URL = "http://10.100.102.4:8084/ambient-intelligence";


     useEffect(() => {
      console.log("user is:", user.userId.email, user.userId.systemID);
      }, []);

  const buildObjectBoundaryFromTag = (data) => {
  return {
    type: data.type,
    alias: data.name,
    status: "IN_STOCK",
    active: true,
    createdBy: {
        email: "operator@example.com",
        systemID: user.userId.systemID
    },
    objectDetails: {
      expiration: data.expiration,
      amount: data.type === 'PRODUCT_BY_QUANTITY' ? data.quantity : data.weight,
    }
  };
};


  const scanNfcTag = async () => {
    console.log("Waiting for NFC tag...");
    try {
      await NfcManager.requestTechnology(NfcTech.Ndef);
      const tag = await NfcManager.getTag();

      if (tag?.ndefMessage) {
        const payload = tag.ndefMessage[0].payload;
        const text = Ndef.text.decodePayload(payload);
        console.log("Raw NFC Text:", text);

        try {
          const parsed = JSON.parse(text);
          setProductData(parsed);
          console.log("Parsed NFC Data:", parsed);
        } catch (err) {
          console.error("Failed to parse JSON:", err.message);
        }
      } else {
        Alert.alert("error", "No content inside the nfc tag");
      }
    } catch (err) {
      console.warn("NFC Error", err);
      Alert.alert("error", "error while reading the tag");
    } finally {
      NfcManager.cancelTechnologyRequest();
    }
  };

const addToInventory = async () => {
  const object = buildObjectBoundaryFromTag(productData, user); 
  const operatorUrl = `${USER_BASE}/${user.userId.systemID}/${user.userId.email}`;
  const endUserUrl  = operatorUrl;  

  const bodyOperator = {   
    userId: {
    email: user.userId.email,
    systemID: user.userId.systemID
    },
    role: "OPERATOR",
    userName: "Operator",
    avatar: "avatarOperator"
    };
    const bodyEndUser  = {
      userId: {
    email: user.userId.email,
    systemID: user.userId.systemID
    },
    role: "END_USER",
    userName: "EndUser",
    avatar: "avatarUser"
    };

    try {
    if(user.role === 'END_USER') {
    const opRes = await axios.put(operatorUrl, bodyOperator);

    const objRes = await axios.post(
      `${BASE_URL}/objects`,
      object
    );

    const endRes = await axios.put(endUserUrl, bodyEndUser);
  }
  else {
    const objRes = await axios.post(
      `${BASE_URL}/objects`,
      object
    );
  }
    Alert.alert("Success", "Product added!");
    navigation.goBack();

  } catch (err) {
    console.error("Error in handleSubmit:", err.response?.data || err.message);
    Alert.alert("Error", err.response?.data?.message || err.message);
  }
};

  return (
    <View style={styles.container}>
      <Text style={styles.title}> Get close to a product to scan it</Text>
      <Button title="Scan NFC" onPress={scanNfcTag} />

      {productData && (
        <View style={styles.card}>
          <Text>Product : {productData.name}</Text>
          <Text>Type: {productData.type}</Text>
          <Text>Amount: {productData.quantity || productData.weight}</Text>
          <Text>Expiration: {productData.expiration}</Text>
          <Button title="Add To Inventory" onPress={addToInventory} />
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#fff',
  },
  title: {
    fontSize: 18, marginBottom: 20,
  },
  card: {
    marginTop: 20, padding: 16, backgroundColor: '#eee', borderRadius: 10, width: '90%',
  },
});

export default ScanProductScreen