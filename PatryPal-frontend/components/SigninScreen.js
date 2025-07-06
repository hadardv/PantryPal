import React, { useState } from 'react';
import {
  SafeAreaView,
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  KeyboardAvoidingView,
  Platform,
  StatusBar,
  Image
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import axios from 'axios';

export default function SigninScreen() {
  const navigation = useNavigation();
  const [userEmail, setEmail] = useState('');
  const [systemId, setSystemId] = useState('');

  const BASE_URL = "http://10.100.102.4:8084/ambient-intelligence";


  async function checkUserValid() {
    try {
      const { data } = await axios.get(
        `${BASE_URL}/users/login/${systemId}/${userEmail}`
      );
      if (!data) {
        Alert.alert('Oops', 'User not found');
        return;
      }
      if(data.role === 'ADMIN') {
        navigation.navigate('AdminHome', { user: data });
      } else {
      navigation.navigate('Home', { user: data });
      }
    } catch (e) {
      Alert.alert('Error', e.message);
    }
  }

  return (
    <>
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" />
      <KeyboardAvoidingView
        style={styles.container}
        behavior={'height'}
      >
        
         <Image
          source={require('../assets/logo.png')}
          style={styles.logo}
          resizeMode="contain"
        />

        <Text style={styles.title}>Welcome Back!</Text>
      
        <View style={styles.formCard}>
          <TextInput
            style={styles.input}
            placeholder="Email"
            placeholderTextColor="#888"
            keyboardType="email-address"
            autoCapitalize="none"
            value={userEmail}
            onChangeText={setEmail}
          />

          <TextInput
            style={styles.input}
            placeholder="System ID"
            placeholderTextColor="#888"
            autoCapitalize="none"
            value={systemId}
            onChangeText={setSystemId}
          />

          <TouchableOpacity
            style={styles.submitButton}
            onPress={checkUserValid}
            activeOpacity={0.8}
          >
            <Text style={styles.submitButtonText}>Sign In</Text>
          </TouchableOpacity>

          <TouchableOpacity
            onPress={() => navigation.navigate('Signup')}
            style={styles.footerLinkContainer}
          >
            <Text style={styles.footerText}>
              Don’t have an account?{' '}
              <Text style={styles.footerLink}>Sign Up</Text>
            </Text>
          </TouchableOpacity>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
    </>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f2f2f2'
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 24
  },
   logo: {
    width: 200,
    height: 200,
    borderRadius: 50,
    alignSelf: 'center',
    marginBottom: 24,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  title: {
    fontSize: 28,
    fontWeight: '600',
    color: '#333',
    textAlign: 'center',
    marginBottom: 32
  },
  formCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    // Android shadow
    elevation: 3
  },
  input: {
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 12,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#e0e0e0'
  },
  submitButton: {
    backgroundColor: '#90D1CA',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
    marginBottom: 12
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600'
  },
  footerLinkContainer: {
    alignItems: 'center',
    marginTop: 4
  },
  footerText: {
    color: '#666',
    fontSize: 14
  },
  footerLink: {
    color: '#90D1CA',
    fontWeight: '600'
  }
});
