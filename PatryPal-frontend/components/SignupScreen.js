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
  Image,
  ScrollView,
  Alert
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { useNavigation } from '@react-navigation/native';
import axios from 'axios';

const avatarOptions = [
  { name: 'avatarAdmin', src: require('../assets/avatar-admin.png') },
  { name: 'avatarOperator', src: require('../assets/avatar-operator.png') },
  { name: 'avatarUser', src: require('../assets/avatar-user.png') }
];

export default function SignupScreen() {
  const navigation = useNavigation();
  const [userEmail, setUserEmail] = useState('');
  const [userName, setUserName] = useState('');
  const [role, setRole] = useState('ADMIN');
  const [avatar, setAvatar] = useState(avatarOptions[0].name);

  const BASE_URL = "http://10.100.102.4:8084/ambient-intelligence";


  async function createUser() {
    try {
      const { data } = await axios.post(
        `${BASE_URL}/users`,
        { email: userEmail, userName, role, avatar }
      );
      if (!data) {
        Alert.alert('Oops', `Couldn't create user`);
        return;
      }
      Alert.alert('Success', 'User created!');
      if(data.role === 'ADMIN') {
        navigation.navigate('AdminHome', { user: data });
      } else {
      navigation.navigate('Home', { user: data });
      }
    } catch (e) {
      Alert.alert('Error', e.message);
    }
  }

  const selectedAvatar = avatarOptions.find(a => a.name === avatar).src;

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" />
      <KeyboardAvoidingView
        style={styles.container}
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      >
        {/* Logo */}
        <Image
          source={require('../assets/logo.png')}
          style={styles.logo}
          resizeMode="contain"
        />

        <Text style={styles.title}>Create Account</Text>
<ScrollView>
        <View style={styles.formCard}>
          <TextInput
            style={styles.input}
            placeholder="Email"
            placeholderTextColor="#888"
            keyboardType="email-address"
            autoCapitalize="none"
            value={userEmail}
            onChangeText={setUserEmail}
          />

          <TextInput
            style={styles.input}
            placeholder="Username"
            placeholderTextColor="#888"
            value={userName}
            onChangeText={setUserName}
          />

          <Text style={styles.label}>Select Role</Text>
          <View style={styles.pickerWrapper}>
            <Picker
              selectedValue={role}
              onValueChange={setRole}
              style={styles.picker}
            >
              <Picker.Item label="Admin" value="ADMIN" />
              <Picker.Item label="Operator" value="OPERATOR" />
              <Picker.Item label="End User" value="END_USER" />
            </Picker>
          </View>
          

          {/* Avatar Selection */}
<Text style={styles.label}>Choose Avatar</Text>
<ScrollView
  horizontal
  showsHorizontalScrollIndicator={false}
  style={styles.avatarContainer}
>
  {avatarOptions.map(item => (
    <TouchableOpacity
      key={item.name}
      onPress={() => setAvatar(item.name)}
      style={[
        styles.avatarWrapper,
        avatar === item.name && styles.avatarSelected
      ]}
    >
      <Image source={item.src} style={styles.avatarImage} />
    </TouchableOpacity>
  ))}
</ScrollView>


          <TouchableOpacity
            style={styles.submitButton}
            onPress={createUser}
            activeOpacity={0.8}
          >
            <Text style={styles.submitButtonText}>Sign Up</Text>
          </TouchableOpacity>

          <TouchableOpacity
            onPress={() => navigation.navigate('Signin')}
            style={styles.footerLinkContainer}
          >
            <Text style={styles.footerText}>
              Already have an account?{' '}
              <Text style={styles.footerLink}>Sign In</Text>
            </Text>
          </TouchableOpacity>
        </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f2f2f2',
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 24,
  },
  logo: {
    width: 200,
    height: 200,
    alignSelf: 'center',
    marginBottom: 20,
    borderRadius: 20,         // rounded edges
    overflow: 'hidden',       // ensure clipping on Android
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  title: {
    fontSize: 26,
    fontWeight: '600',
    color: '#333',
    textAlign: 'center',
    marginBottom: 28,
  },
  formCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  input: {
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 12,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#e0e0e0',
  },
  label: {
    fontSize: 14,
    fontWeight: '500',
    color: '#555',
    marginBottom: 8
  },
  pickerWrapper: {
    borderWidth: 1,
    borderColor: '#e0e0e0',
    borderRadius: 8,
    marginBottom: 16,
    overflow: 'hidden'
  },
  picker: {
    height: 50,
    width: '100%'
  },
  avatarContainer: {
    marginBottom: 24
  },
  avatarWrapper: {
    width: 60,
    height: 60,
    borderRadius: 30,
    marginRight: 12,
    overflow: 'hidden',
    borderWidth: 2,
    borderColor: 'transparent'
  },
  avatarSelected: {
    borderColor: '#4E8EEB'
  },
  avatarImage: {
    width: '100%',
    height: '100%'
  },
  submitButton: {
    backgroundColor: '#90D1CA',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
    marginBottom: 12,
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  footerLinkContainer: {
    alignItems: 'center',
    marginTop: 4,
  },
  footerText: {
    color: '#666',
    fontSize: 14,
  },
  footerLink: {
    color: '#90D1CA',
    fontWeight: '600',
  },
});
