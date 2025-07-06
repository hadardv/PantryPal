import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Alert,
  Image,
  ScrollView,
  Modal
} from 'react-native';
import {useNavigation} from '@react-navigation/native';

import axios from 'axios';

const AdminHomeScreen = ({route}) => {
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('');
  const [modalData, setModalData] = useState([]);
  const {user} = route.params;
  const navigation = useNavigation();
  const ADMIN_BASE = 'http://10.100.102.4:8084/ambient-intelligence/admin';

  const config = () => ({
    params: {
      userSystemId: user.userId.systemID,
      userEmail: user.userId.email,
    },
  });

  const exportUsers = async () => {
    try {
      const {data} = await axios.get(`${ADMIN_BASE}/users`, config());
      setModalTitle('All Users');
      setModalData(data);
      setModalVisible(true);
    } catch (e) {
      Alert.alert('Error', e.response?.data?.message || e.message);
    }
  };

  const deleteUsers = async () => {
    Alert.alert('Confirm Delete', 'Erase all users?', [
      {text: 'Cancel', style: 'cancel'},
      {
        text: 'OK',
        onPress: async () => {
          try {
            await axios.delete(`${ADMIN_BASE}/users`, config());
            Alert.alert('Done', 'All users deleted');
          } catch (e) {
            Alert.alert('Error', e.response?.data?.message || e.message);
          }
        },
      },
    ]);
  };

  const exportCommands = async () => {
    try {
      const {data} = await axios.get(`${ADMIN_BASE}/commands`, config());
      setModalTitle('All Commands');
      setModalData(data);
      setModalVisible(true);
    } catch (e) {
      Alert.alert('Error', e.response?.data?.message || e.message);
    }
  };

  const deleteCommands = async () => {
    Alert.alert('Confirm Delete', 'Erase all commands?', [
      {text: 'Cancel', style: 'cancel'},
      {
        text: 'OK',
        onPress: async () => {
          try {
            await axios.delete(`${ADMIN_BASE}/commands`, config());
            Alert.alert('Done', 'All commands deleted');
          } catch (e) {
            Alert.alert('Error', e.response?.data?.message || e.message);
          }
        },
      },
    ]);
  };

  const deleteObjects = async () => {
    Alert.alert('Confirm Delete', 'Erase all objects?', [
      {text: 'Cancel', style: 'cancel'},
      {
        text: 'OK',
        onPress: async () => {
          try {
            await axios.delete(`${ADMIN_BASE}/objects`, config());
            Alert.alert('Done', 'All objects deleted');
          } catch (e) {
            Alert.alert('Error', e.response?.data?.message || e.message);
          }
        },
      },
    ]);
  };

 
  return (
    <>
        <Modal
      visible={modalVisible}
      animationType="slide"
      transparent={true}
      onRequestClose={() => setModalVisible(false)}>
      <View
        style={{
          flex: 1,
          backgroundColor: 'rgba(0,0,0,0.4)',
          justifyContent: 'center',
          alignItems: 'center',
        }}>
        <View
          style={{
            width: '90%',
            maxHeight: '80%',
            backgroundColor: '#fff',
            borderRadius: 12,
            padding: 20,
          }}>
          <Text style={{fontSize: 22, fontWeight: 'bold', marginBottom: 12}}>
            {modalTitle}
          </Text>
          <ScrollView style={{maxHeight: 400}}>
            {modalData.length === 0 && <Text>No data to display.</Text>}
            {modalData.map((item, i) => (
              <View
                key={i}
                style={{
                  padding: 12,
                  backgroundColor: i % 2 ? '#f6fafd' : '#e7f9f8',
                  marginBottom: 6,
                  borderRadius: 6,
                }}>
                {Object.entries(item).map(([key, value]) => (
                  <Text key={key} style={{fontSize: 15}}>
                    <Text style={{fontWeight: 'bold'}}>{key}: </Text>
                    {typeof value === 'object'
                      ? JSON.stringify(value)
                      : String(value)}
                  </Text>
                ))}
              </View>
            ))}
          </ScrollView>
          <TouchableOpacity
            style={{marginTop: 18, alignSelf: 'center', padding: 10}}
            onPress={() => setModalVisible(false)}>
            <Text style={{color: '#007AFF', fontWeight: '600', fontSize: 16}}>
              Close
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
    <ScrollView contentContainerStyle={styles.container}>
      {/* Top Bar */}
      <View style={styles.topBar}>
        <Text style={styles.greeting}>Hello, {user.userName} 👋</Text>
        <View style={styles.topButtons}>
          <TouchableOpacity
            onPress={() => navigation.navigate('UserProfile', {user})}>
            <Image
              source={require('../assets/user2.png')}
              style={styles.profileIcon}
            />
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.signOutButton}
            onPress={() => navigation.replace('Signin')}>
            <Text style={styles.signOutText}>Sign Out</Text>
          </TouchableOpacity>
        </View>
      </View>
      <Text style={styles.heading}>⚙️ Admin Panel</Text>

      <TouchableOpacity style={styles.button} onPress={exportUsers}>
        <Text style={styles.buttonText}>Export All Users</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button} onPress={deleteUsers}>
        <Text style={styles.buttonText}>Delete All Users</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button} onPress={exportCommands}>
        <Text style={styles.buttonText}>Export All Commands</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button} onPress={deleteCommands}>
        <Text style={styles.buttonText}>Delete All Commands</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button} onPress={deleteObjects}>
        <Text style={styles.buttonText}>Delete All Objects</Text>
      </TouchableOpacity>
    </ScrollView>
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 20,
    alignItems: 'stretch',
  },
  heading: {
    fontSize: 24,
    fontWeight: '600',
    marginBottom: 16,
    textAlign: 'center',
  },
  topBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
  },
  greeting: {fontSize: 22, fontWeight: '600', color: '#333'},
  topButtons: {flexDirection: 'row', alignItems: 'center'},
  profileIcon: {width: 36, height: 36, marginRight: 12},
  signOutButton: {
    backgroundColor: '#90D1CA',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 8,
  },
  signOutText: {color: '#fff', fontSize: 14, fontWeight: '500'},
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginHorizontal: 16,
    marginVertical: 12,
  },
  button: {
    backgroundColor: '#90D1CA',
    paddingVertical: 14,
    borderRadius: 8,
    marginVertical: 8,
    alignItems: 'center',
  },
  buttonText: {
    color: '#fff',
    fontWeight: '500',
  },
});
export default AdminHomeScreen;
