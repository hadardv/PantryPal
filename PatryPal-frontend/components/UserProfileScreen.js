import React, { useEffect, useState } from 'react';
import {
  SafeAreaView,
  View,
  Text,
  StyleSheet,
  Image,
  TouchableOpacity,
  FlatList,
  StatusBar,
  ActivityIndicator,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import axios from 'axios';



export default function UserProfileScreen({ route }) {
  const { user } = route.params;
  const navigation = useNavigation();
  const [objects, setObjects] = useState(null);

  const BASE_URL = "http://10.100.102.4:8084/ambient-intelligence";


  const avatarMap = {
  avatarAdmin: require('../assets/avatar-admin.png'),
  avatarOperator: require('../assets/avatar-operator.png'),
  avatarUser: require('../assets/avatar-user.png')
};

  console.log(avatarMap[user.avatar])

  useEffect(() => {
    console.log(user)
  if (user.role === 'ADMIN') return;
    (async () => {
      try {
        const { data } = await axios.get(
          `${BASE_URL}/objects`,
          {
            params: {
              userSystemID: user.userId.systemID,
              userEmail: user.userId.email,
            },
          }
        );
        setObjects(data);
      } catch (e) {
        console.error(e);
        setObjects([]); 
      }
    })();
  }, []);

  const topItems = React.useMemo(() => {
    if (!objects) return [];
    const freq = {};
    objects.filter(
      (o) =>
        o.type === "PRODUCT_BY_WEIGHT" ||
        o.type === "PRODUCT_BY_QUANTITY"
    )
    .forEach(o => {
      const key = o.alias;
      const amt = o.objectDetails.amount;
      freq[key] = (freq[key] || 0) + amt;
    });
    return Object.entries(freq)
      .map(([alias, count]) => ({ alias, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);
  }, [objects]);

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" />
      <View style={styles.header}>
        <View style={styles.headerBg} />
        <Image
          source={avatarMap[user.avatar] || require('../assets/user.png')}
          style={styles.profilePicture}
        />
      </View>

      <View style={styles.card}>
        <Text style={styles.name}>{user.userName}</Text>
        <Text style={styles.role}>{user.role}</Text>
      </View>
    {(user.role === 'END_USER' || user.role === 'OPERATOR') && 
      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Top 5 Added Items</Text>

        {objects === null ? (
          <ActivityIndicator style={{ marginTop: 20 }} />
        ) : topItems.length === 0 ? (
          <Text style={styles.emptyText}>No items yet.</Text>
        ) : (
          <FlatList
            data={topItems}
            keyExtractor={i => i.alias}
            renderItem={({ item }) => (
              <View style={styles.itemRow}>
                <Text style={styles.itemName}>{item.alias}</Text>
                <Text style={styles.itemCount}>x{item.count}</Text>
              </View>
            )}
          />
        )}
      </View>
}
    {(user.role === 'END_USER' || user.role === 'OPERATOR') && 

      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('ViewTrends', { user })}
      >
        <Text style={styles.buttonText}>View User Trends</Text>
      </TouchableOpacity> }
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#f2f2f2' },
  header: { height: 160, alignItems: 'center', justifyContent: 'flex-end' },
  headerBg: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: '#90D1CA',
    borderBottomLeftRadius: 40,
    borderBottomRightRadius: 40,
  },
  profilePicture: {
    width: 100,
    height: 100,
    borderRadius: 50,
    borderWidth: 3,
    borderColor: '#fff',
    position: 'absolute',
    bottom: -50,
  },
  card: {
    backgroundColor: '#fff',
    marginHorizontal: 20,
    marginTop: 60,
    borderRadius: 12,
    padding: 16,
    // shadow
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },
  name: { fontSize: 22, fontWeight: '600', textAlign: 'center' },
  role: { fontSize: 14, color: '#777', textAlign: 'center', marginTop: 4 },
  sectionTitle: { fontSize: 16, fontWeight: '600', marginBottom: 12 },
  emptyText: { textAlign: 'center', color: '#999' },
  itemRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  itemName: { fontSize: 14, color: '#333' },
  itemCount: { fontSize: 14, fontWeight: '500', color: '#90D1CA' },
  button: {
    backgroundColor: '#90D1CA',
    margin: 20,
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
  },
  buttonText: { color: '#fff', fontSize: 16, fontWeight: '600' },
});
