import React, { useState } from "react";
import {
  View, Text, StyleSheet,
  Image, TouchableOpacity,
  Alert
} from "react-native";
import { TextInput } from "react-native";
import axios from "axios";

export default function AddProductScreen({ route,navigation }) {
  const { user } = route.params;
  const [mode, setMode] = useState("quantity");
  const [productName, setProductName] = useState("");
  const [amount, setAmount] = useState("");
  const [expirationDate, setExpirationDate] = useState("");

  const USER_BASE = 
  "http://10.100.102.4:8084/ambient-intelligence/users";

  const BASE_URL = "http://10.100.102.4:8084/ambient-intelligence";



  const handleSubmit = async () => {
    if (!productName.trim()) {
      return Alert.alert("Validation", "Please enter product name.");
    }
    const parsedAmount =
      mode === "quantity" ? parseInt(amount, 10) : parseFloat(amount);
    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      return Alert.alert(
        "Validation",
        `Please enter a valid ${mode === "quantity" ? "number" : "weight"}.`
      );
    }
    if (!expirationDate.match(/^\d{4}-\d{2}-\d{2}$/)) {
      return Alert.alert(
        "Validation",
        "Please enter expiration in YYYY-MM-DD format."
      );
    }

    const newObject = {
      type: mode === "quantity" ? "PRODUCT_BY_QUANTITY" : "PRODUCT_BY_WEIGHT",
      alias: productName.trim(),
      status: "IN_STOCK",
      active: true,
      createdBy: {
          email: "operator@example.com",
          systemID: user.userId.systemID,
      },
      objectDetails: {
        expiration: expirationDate,
        amount: parsedAmount,
      },
    };
    console.log(newObject)

    const operatorUrl = `${USER_BASE}/${user.userId.systemID}/${user.userId.email}`;
    const endUserUrl  = operatorUrl;  

    console.log(user)

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
    const opRes = await axios.put(operatorUrl, bodyOperator);

    const objRes = await axios.post(
      `${BASE_URL}/objects`,
      newObject
    );

    const endRes = await axios.put(endUserUrl, bodyEndUser);
    Alert.alert("Success", "Product added!");
    navigation.goBack();

  } catch (err) {
    console.error("Error in handleSubmit:", err.response?.data || err.message);
    Alert.alert("Error", err.response?.data?.message || err.message);
  }
    
  };

  return (
    <View style={styles.container}>
      <View style={styles.toggleContainer}>
        <TouchableOpacity
          onPress={() => setMode("quantity")}
          style={[
            styles.toggleButton,
            mode === "quantity" && styles.activeToggle,
          ]}
        >
          <Text
            style={
              mode === "quantity" ? styles.activeText : styles.inactiveText
            }
          >
            By Quantity
          </Text>
        </TouchableOpacity>
        <TouchableOpacity
          onPress={() => setMode("weight")}
          style={[
            styles.toggleButton,
            mode === "weight" && styles.activeToggle,
          ]}
        >
          <Text
            style={mode === "weight" ? styles.activeText : styles.inactiveText}
          >
            By Weight
          </Text>
        </TouchableOpacity>
      </View>

      <View style={styles.formCard}>
        <Text style={styles.label}>Product Name</Text>
        <TextInput
          style={styles.input}
          value={productName}
          onChangeText={setProductName}
          placeholder="e.g. Milk"
        />

        <Text style={styles.label}>
          {mode === "quantity" ? "Quantity" : "Weight"}
        </Text>
        <TextInput
          style={styles.input}
          value={amount}
          onChangeText={setAmount}
          placeholder={mode === "quantity" ? "e.g. 2" : "e.g. 1.5"}
          keyboardType="numeric"
        />

        <Text style={styles.label}>Expiration Date</Text>
        <TextInput
          style={styles.input}
          value={expirationDate}
          onChangeText={setExpirationDate}
          placeholder="YYYY-MM-DD"
        />
      </View>

      <TouchableOpacity style={styles.submitButton} onPress={handleSubmit}>
        <Text style={styles.submitText}>Add Product</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    marginTop: 50,
    backgroundColor: "#fff",
  },
  toggleContainer: {
    flexDirection: "row",
    justifyContent: "center",
    marginBottom: 20,
  },
  toggleButton: {
    flex: 1,
    paddingVertical: 10,
    marginHorizontal: 5,
    borderRadius: 20,
    backgroundColor: "#ccc",
    alignItems: "center",
  },
  activeToggle: {
    backgroundColor: "#90D1CA",
  },
  activeText: {
    color: "#fff",
    fontWeight: "bold",
  },
  inactiveText: {
    color: "#333",
  },
  formCard: {
    backgroundColor: "#f9f9f9",
    padding: 20,
    borderRadius: 16,
    borderColor: "#ccc",
    borderWidth: 1,
    marginBottom: 20,
  },
  label: {
    marginTop: 10,
    fontSize: 14,
    fontWeight: "600",
  },
  input: {
    backgroundColor: "#eee",
    borderRadius: 8,
    padding: 10,
    marginVertical: 8,
  },
  submitButton: {
    backgroundColor: "#90D1CA",
    paddingVertical: 12,
    borderRadius: 10,
    alignItems: "center",
  },
  submitText: {
    color: "#fff",
    fontWeight: "bold",
  },
});
