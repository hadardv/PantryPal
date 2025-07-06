import React, {useEffect, useState} from 'react';
import {
  SafeAreaView,
  View,
  Text,
  StyleSheet,
  Image,
  TouchableOpacity,
  FlatList,
  StatusBar,
  ScrollView,
  Button,
} from 'react-native';
import {useNavigation, useFocusEffect} from '@react-navigation/native';
import axios from 'axios';

export default function HomeScreen({route}) {
  const {user} = route.params;
  const navigation = useNavigation();
  const [objects, setObjects] = useState([]);
  const [shoppingList, setShoppingList] = useState([]);
  const [weeklyExpierdCount, setWeeklyExpierdCount] = useState(0);

  const BASE_URL = 'http://10.100.102.4:8084/ambient-intelligence';
     const USER_BASE = 
  "http://10.100.102.4:8084/ambient-intelligence/users";

  useEffect(() => {
    buildShoppingList();
  }, []);

  useEffect(() => {
  fetchWeeklyExpierd();
}, [user.userId.systemID, user.userId.email, user.role]);

  useFocusEffect(
    React.useCallback(() => {
      let isActive = true;
      (async () => {
        try {
          const {data} = await axios.get(`${BASE_URL}/objects`, {
            params: {
              userSystemID: user.userId.systemID,
              userEmail: user.userId.email,
            },
          });
          if (isActive) {
            const filtered = data.filter(
              o =>
                o.type === 'PRODUCT_BY_QUANTITY' ||
                o.type === 'PRODUCT_BY_WEIGHT',
            );
            setObjects(filtered);
          }
        } catch (e) {
          console.error(e);
        }
      })();
      return () => {
        isActive = false;
        setObjects([]);
      };
    }, [user.userId.systemID, user.userId.email]),
  );

  const buildShoppingList = async () => {
    try {
      const commandPayload = {
        command: 'getExpiredOrOutOfStockProducts',
        targetObject: {
          objectId: '',
          systemID: '',
        },
        invocationTimestamp: new Date().toISOString(),
        invokedBy: {
          email: user.userId.email,
          systemID: user.userId.systemID,
        },
        commandAttributes: {},
      };
      const response = await axios.post(
        `${BASE_URL}/commands`,
        commandPayload,
        {headers: {'Content-Type': 'application/json'}},
      );
      const allExpiredOutOfStock = response.data;
      console.log(allExpiredOutOfStock);
      setShoppingList(allExpiredOutOfStock);
    } catch (error) {
      throw new Error(error);
    }
  };

  const fetchWeeklyExpierd = async () => {
    try {
      const operatorUrl = `${USER_BASE}/${user.userId.systemID}/${user.userId.email}`;
      const endUserUrl = operatorUrl;

      const bodyOperator = {
        userId: {
          email: user.userId.email,
          systemID: user.userId.systemID,
        },
        role: 'OPERATOR',
        userName: 'Operator',
        avatar: 'avatarOperator',
      };
      const bodyEndUser = {
        userId: {
          email: user.userId.email,
          systemID: user.userId.systemID,
        },
        role: 'END_USER',
        userName: 'EndUser',
        avatar: 'avatarUser',
      };

      if (user.role === 'OPERATOR') {
        const endRes = await axios.put(endUserUrl, bodyEndUser);

        const payload = {
          commandId: {commandId: '', systemID: user.userId.systemID},
          command: 'getProductsExpiringInNextWeek',
          targetObject: {objectId: '', systemID: ''},
          invocationTimestamp: new Date().toISOString(),
          invokedBy: {email: user.userId.email, systemID: user.userId.systemID},
          commandAttributes: {},
        };
        const res = await axios.post(`${BASE_URL}/commands`, payload, {
          headers: {'Content-Type': 'application/json'},
        });
        setWeeklyExpierdCount(res.data.length);
        const opRes = await axios.put(operatorUrl, bodyOperator);
      } else {
        const payload = {
          commandId: {commandId: '', systemID: user.userId.systemID},
          command: 'getProductsExpiringInNextWeek',
          targetObject: {objectId: '', systemID: ''},
          invocationTimestamp: new Date().toISOString(),
          invokedBy: {email: user.userId.email, systemID: user.userId.systemID},
          commandAttributes: {},
        };
        const res = await axios.post(`${BASE_URL}/commands`, payload, {
          headers: {'Content-Type': 'application/json'},
        });
        setWeeklyExpierdCount(res.data.length);
      }
    } catch (err) {
      console.error('fetchWeekly error:', err);
    }
  };

  const stats = [
    {
      key: 'items',
      label: 'Items',
      value: objects.length,
      icon: require('../assets/items.png'),
    },
    {
      key: 'shopping',
      label: 'Shopping',
      value: shoppingList.length,
      icon: require('../assets/shopping-cart.png'),
    },
    {
      key: 'expiring',
      label: 'Expiring Soon',
      value: weeklyExpierdCount,
      icon: require('../assets/warning.png'),
    },
  ];

const renderObject = ({item}) => {
  const status = getExpirationStatus(item.objectDetails.expiration);

  let icon = null;
  if (status === "expired" || status === "expiringToday") {
    icon = require('../assets/red-warning.png');
  } else if (status === "expiringSoon") {
    icon = require('../assets/expire.png');
  }

  return (
    <View style={styles.inventoryItem}>
      <Text style={styles.inventoryText}>
        {item.alias} x{item.objectDetails.amount}
      </Text>
      {icon && (
        <Image
          source={icon}
          style={styles.inventoryIcon}
        />
      )}
    </View>
  );
};

  function getExpirationStatus(expiration) {
  const now = new Date();
  now.setHours(0, 0, 0, 0);
  const exp = new Date(expiration);
  exp.setHours(0, 0, 0, 0);

  const diffDays = Math.ceil((exp - now) / (1000 * 60 * 60 * 24));

  if (diffDays < 0) {
    return "expired"; 
  } else if (diffDays === 0) {
    return "expiringToday";
  } else if (diffDays <= 7) {
    return "expiringSoon";
  }
  return "ok";
}


  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" />

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

      {/* Stats Cards */}
      <View style={styles.statsContainer}>
        {stats.map(s => (
          <View key={s.key} style={styles.statCard}>
            <Image source={s.icon} style={styles.statIcon} />
            <Text style={styles.statValue}>{s.value}</Text>
            <Text style={styles.statLabel}>{s.label}</Text>
          </View>
        ))}
      </View>

      {/* Inventory Section */}
      {(user.role === 'OPERATOR' || user.role === 'END_USER') && (
        <View style={styles.inventoryCard}>
          <View style={styles.inventoryHeader}>
            <View>
              <Text style={styles.inventoryTitle}>My Inventory</Text>
              <Text style={styles.inventorySubtitle}>Recently Added</Text>
            </View>
            <TouchableOpacity
              onPress={() =>
                navigation.navigate('InventoryList', {objects, user})
              }>
              <Text style={styles.inventoryLink}>View List</Text>
            </TouchableOpacity>
          </View>
          <FlatList
            data={objects.slice(0, 5)}
            keyExtractor={i => i.objectId}
            renderItem={renderObject}
            style={styles.inventoryList}
            ItemSeparatorComponent={() => <View style={styles.separator} />}
          />
        </View>
      )}

      {/* Shopping List Section */}
      {user.role === 'END_USER' && (
        <View style={styles.inventoryCard}>
          <View style={styles.inventoryHeader}>
            <View>
              <Text style={styles.inventoryTitle}>My Shopping List</Text>
              <Text style={styles.inventorySubtitle}>
                Expired & Out‐of‐Stock
              </Text>
            </View>
            <TouchableOpacity onPress={buildShoppingList}>
              <Text style={styles.inventoryLink}>Refresh</Text>
            </TouchableOpacity>
          </View>

          {shoppingList.length === 0 ? (
            <View style={{paddingVertical: 20}}>
              <Text style={styles.emptyText}>
                No expired or out‐of‐stock items.
              </Text>
            </View>
          ) : (
            <FlatList
              data={shoppingList}
              keyExtractor={item =>
                item.id?.objectId ?? Math.random().toString()
              }
              renderItem={({item}) => (
                <View style={styles.inventoryItem}>
                  <Text style={styles.inventoryText}>
                    {item.alias} x{item.objectDetails.amount}
                  </Text>
                  {/* <Image
            source={require('../assets/red-warning.png')}
            style={styles.inventoryIcon}
          /> */}
                </View>
              )}
              ItemSeparatorComponent={() => <View style={styles.separator} />}
              style={styles.inventoryList}
            />
          )}
        </View>
      )}

      {/* Action Buttons */}
      <View style={styles.actionsContainer}>
        {(user.role === 'OPERATOR' || user.role === 'END_USER') && (
          <TouchableOpacity
            style={styles.actionButton}
            onPress={() => navigation.navigate('ScanProduct', {user})}>
            <Image
              source={require('../assets/scan.png')}
              style={styles.actionIcon}
            />
            <Text style={styles.actionText}>Scan</Text>
          </TouchableOpacity>
        )}

        {(user.role === 'OPERATOR' || user.role === 'END_USER') && (
          <TouchableOpacity
            style={styles.actionButton}
            onPress={() => navigation.navigate('AddProduct', {user})}>
            <Image
              source={require('../assets/plus.png')}
              style={styles.actionIcon}
            />
            <Text style={styles.actionText}>Add Item</Text>
          </TouchableOpacity>
        )}

        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => navigation.navigate('ViewTrends', {user})}>
          <Image
            source={require('../assets/graph.png')}
            style={styles.actionIcon}
          />
          <Text style={styles.actionText}>Trends</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {flex: 1, padding: 16, backgroundColor: '#fff'},
  header: {fontSize: 24, fontWeight: 'bold', marginBottom: 12},
  emptyText: {
    fontSize: 16,
    color: '#666',
    marginVertical: 20,
    textAlign: 'center',
  },
  card: {
    padding: 12,
    marginVertical: 6,
    marginHorizontal: 4,
    borderRadius: 8,
    backgroundColor: '#f9f9f9',
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  productName: {fontSize: 18, fontWeight: '600'},
  safeArea: {flex: 1, backgroundColor: '#f2f2f2'},
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
  statCard: {
    flex: 1,
    backgroundColor: '#fff',
    marginHorizontal: 4,
    borderRadius: 12,
    alignItems: 'center',
    padding: 12,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },
  statIcon: {width: 28, height: 28, marginBottom: 8},
  statValue: {fontSize: 20, fontWeight: '600', color: '#333'},
  statLabel: {fontSize: 12, color: '#777'},
  inventoryCard: {
    backgroundColor: '#fff',
    margin: 16,
    borderRadius: 12,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  inventoryHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  inventoryTitle: {fontSize: 18, fontWeight: '600', color: '#333'},
  inventorySubtitle: {fontSize: 12, color: '#aaa'},
  inventoryLink: {color: '#90D1CA', fontSize: 14, fontWeight: '500'},
  inventoryList: {maxHeight: 200},
  inventoryItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 8,
  },
  inventoryText: {fontSize: 14, color: '#444'},
  inventoryIcon: {width: 20, height: 20},
  separator: {height: 1, backgroundColor: '#eee'},
  actionsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 20,
    paddingHorizontal: 16,
  },
  actionButton: {
    backgroundColor: '#90D1CA',
    width: 90,
    height: 90,
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
  },
  actionIcon: {width: 30, height: 30, marginBottom: 6, tintColor: '#fff'},
  actionText: {
    color: '#fff',
    fontSize: 13,
    fontWeight: '500',
    textAlign: 'center',
  },
});
