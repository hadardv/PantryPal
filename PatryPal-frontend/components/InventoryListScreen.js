import React, {act, useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Modal,
  TextInput,
  Switch,
  Image,
} from 'react-native';
import axios from 'axios';

export default function InventoryListScreen({route}) {
  const {objects, user} = route.params;

  const [products, setProducts] = useState(objects);
  const [modalVisible, setModalVisible] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [newAmount, setNewAmount] = useState('');
  const [newActive, setNewActive] = useState(false);
  const [newExp, setNewExp] = useState('');

  const [filterModalVisible, setFilterModalVisible] = useState(false);
  const [useAlias, setUseAlias] = useState(false);
  const [aliasVal, setAliasVal] = useState('');
  const [useAliasPattern, setUseAliasPattern] = useState(false);
  const [aliasPatternVal, setAliasPatternVal] = useState('');
  const [useType, setUseType] = useState(false);
  const [typeVal, setTypeVal] = useState('PRODUCT_BY_WEIGHT');
  const [useStatus, setUseStatus] = useState(false);
  const [statusVal, setStatusVal] = useState('ACTIVE');

  const BASE_URL = 'http://10.100.102.4:8084/ambient-intelligence';

  function openEditModal(item) {
    setEditItem(item);
    setNewAmount(item.objectDetails.amount.toString());
    setNewExp(item.objectDetails.expiration);
    setNewActive(item.active);
    setModalVisible(true);
  }

  async function saveChanges() {
    const amountNum = parseInt(newAmount, 10);
    if (isNaN(amountNum) || amountNum < 0) {
      alert('Amount must be a non-negative number.');
      return;
    }
    const systemID = editItem.id.systemID;
    const objectId = editItem.id.objectId;
    console.log(user);
    const url = `${BASE_URL}/objects/` + `${systemID}/${objectId}`;

    const body = {
      active: newActive,
      createdBy: {
        email: user.userId.email,
        systemID: user.userId.systemID,
      },
      objectDetails: {
        amount: parseInt(newAmount, 10),
        expiration: newExp,
      },
    };

    const config = {
      params: {
        userSystemID: user.userId.systemID,
        userEmail: user.userId.email,
      },
    };

    try {
      const response = await axios.put(url, body, config);
      setProducts(list =>
        list.map(obj =>
          obj.id.objectId === objectId
            ? {
                ...obj,
                objectDetails: {
                  ...obj.objectDetails,
                  amount: parseInt(newAmount, 10),
                  expiration: newExp,
                },
              }
            : obj,
        ),
      );
      setModalVisible(false);
    } catch (err) {
      console.error('update error:', err.response?.status, err.response?.data);
    }
  }

  async function applyFilters() {
    setFilterModalVisible(false);
    if (!useAlias && !useType && !useStatus && !useAliasPattern) {
      setProducts(objects);
      return;
    }

    let endpoint = '';
    if (useType && useStatus) {
      endpoint = `/objects/search/byTypeAndStatus/${typeVal}/${statusVal}`;
    } else if (useType) {
      endpoint = `/objects/search/byType/${typeVal}`;
    } else if (useStatus) {
      endpoint = `/objects/search/byStatus/${statusVal}`;
    } else if (useAliasPattern) {
      endpoint = `/objects/search/byAliasPattern/${aliasPatternVal}`;
    } else if (useAlias) {
      endpoint = `/objects/search/byAlias/${aliasVal}`;
    }

    try {
      const {data} = await axios.get(`${BASE_URL}${endpoint}`, {
        params: {
          userSystemID: user.userId.systemID,
          userEmail: user.userId.email,
        },
      });
      setProducts(data);
    } catch (error) {
      console.error('filter error:', err.response?.status, err.response?.data);
    }
  }
  function getExpirationStatus(expiration) {
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    const exp = new Date(expiration);
    exp.setHours(0, 0, 0, 0);

    const diffDays = Math.ceil((exp - now) / (1000 * 60 * 60 * 24));

    if (diffDays < 0) {
      return 'expired';
    } else if (diffDays === 0) {
      return 'expiringToday';
    } else if (diffDays <= 7) {
      return 'expiringSoon';
    }
    return 'ok';
  }

  return (
    <View style={styles.container}>
      <Text style={styles.listTitle}>My Products</Text>
      <View style={styles.filterBar}>
        <TouchableOpacity
          style={styles.filterBtn}
          onPress={() => setFilterModalVisible(true)}>
          <Text style={styles.filterBtnText}>Filter</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.resetBtn}
          onPress={() => setProducts(objects)}>
          <Text style={styles.resetBtnText}>Reset</Text>
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.list}>
        {products.map(item => (
          <View key={item.id.objectId} style={styles.card}>
            <Text style={styles.alias}>{item.alias}</Text>
            <Text>Qty: {item.objectDetails.amount}</Text>
            <Text>Active: {item.active.toString()}</Text>
            <Text>Exp: {item.objectDetails.expiration}</Text>
            {(() => {
              const status = getExpirationStatus(item.objectDetails.expiration);
              if (status === 'expired' || status === 'expiringToday') {
                return (
                  <Image
                    source={require('../assets/red-warning.png')}
                    style={styles.inventoryIcon}
                  />
                );
              }
              if (status === 'expiringSoon') {
                return (
                  <Image
                    source={require('../assets/expire.png')}
                    style={styles.inventoryIcon}
                  />
                );
              }
              return null;
            })()}

            {user.role === 'OPERATOR' && (
              <TouchableOpacity
                style={styles.editBtn}
                onPress={() => openEditModal(item)}>
                <Text style={styles.editText}>Edit</Text>
              </TouchableOpacity>
            )}
          </View>
        ))}
      </ScrollView>

      <Modal
        animationType="slide"
        transparent
        visible={modalVisible}
        onRequestClose={() => setModalVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Edit {editItem?.alias}</Text>

            <Text>Amount</Text>
            <TextInput
              style={styles.input}
              keyboardType="numeric"
              value={newAmount}
              onChangeText={text => {
                const filtered = text.replace(/[^0-9]/g, '');
                setNewAmount(filtered);
              }}
            />

            <Text>Expiration (YYYY-MM-DD)</Text>
            <TextInput
              style={styles.input}
              placeholder="2025-06-30"
              value={newExp}
              onChangeText={setNewExp}
            />

            <View style={styles.row}>
              <Text>Active {newActive}</Text>
              <Switch
                value={newActive}
                onValueChange={val => setNewActive(val)}
              />
            </View>

            <View style={styles.modalButtons}>
              <TouchableOpacity
                style={[styles.btn, styles.btnCancel]}
                onPress={() => setModalVisible(false)}>
                <Text>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.btn, styles.btnSave]}
                onPress={saveChanges}>
                <Text style={{color: '#fff'}}>Save</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      <Modal
        animationType="slide"
        transparent
        visible={filterModalVisible}
        onRequestClose={() => setFilterModalVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={[styles.modalContent, {width: 320}]}>
            <Text style={styles.modalTitle}>Filter Items</Text>

            <View style={styles.row}>
              <Switch value={useAlias} onValueChange={setUseAlias} />
              <Text style={styles.switchLabel}>By Alias</Text>
            </View>
            {useAlias && (
              <TextInput
                style={styles.input}
                placeholder="e.g. milk"
                value={aliasVal}
                onChangeText={setAliasVal}
              />
            )}

            <View style={styles.row}>
              <Switch
                value={useAliasPattern}
                onValueChange={setUseAliasPattern}
              />
              <Text style={styles.switchLabel}>By Alias Pattern</Text>
            </View>
            {useAliasPattern && (
              <TextInput
                style={styles.input}
                placeholder="e.g. M for Milk"
                value={aliasPatternVal}
                onChangeText={setAliasPatternVal}
              />
            )}

            <View style={styles.row}>
              <Switch value={useType} onValueChange={setUseType} />
              <Text style={styles.switchLabel}>By Type</Text>
            </View>
            {useType && (
              <TextInput
                style={styles.input}
                placeholder="PRODUCT_BY_WEIGHT"
                value={typeVal}
                onChangeText={setTypeVal}
              />
            )}

            <View style={styles.row}>
              <Switch value={useStatus} onValueChange={setUseStatus} />
              <Text style={styles.switchLabel}>By Status</Text>
            </View>
            {useStatus && (
              <TextInput
                style={styles.input}
                placeholder="ACTIVE"
                value={statusVal}
                onChangeText={setStatusVal}
              />
            )}

            <View style={styles.modalButtons}>
              <TouchableOpacity
                style={[styles.btn, styles.btnCancel]}
                onPress={() => setFilterModalVisible(false)}>
                <Text>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.btn, styles.btnSave]}
                onPress={applyFilters}>
                <Text style={{color: '#fff'}}>Apply</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {flex: 1, padding: 20, backgroundColor: '#fff'},
  listTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 12,
    textAlign: 'center',
  },
  list: {paddingBottom: 40},
  filterBar: {flexDirection: 'row', marginBottom: 12, justifyContent: 'center'},
  filterBtn: {
    padding: 8,
    backgroundColor: '#007AFF',
    borderRadius: 4,
    marginRight: 8,
  },
  filterBtnText: {color: '#fff', fontWeight: '600'},
  resetBtn: {padding: 8, backgroundColor: '#eee', borderRadius: 4},
  resetBtnText: {fontWeight: '600'},
  inventoryIcon: {width: 20, height: 20},

  card: {
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    padding: 12,
    marginBottom: 12,
    position: 'relative',
  },
  alias: {fontSize: 18, fontWeight: '600', marginBottom: 4},
  editBtn: {position: 'absolute', top: 8, right: 8},
  editText: {color: '#007AFF', fontWeight: '600'},

  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.4)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContent: {
    width: 300,
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 20,
  },
  modalTitle: {fontSize: 18, fontWeight: 'bold', marginBottom: 16},
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 4,
    padding: 8,
    marginBottom: 12,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 8,
  },
  switchLabel: {marginLeft: 8},

  modalButtons: {flexDirection: 'row', justifyContent: 'flex-end'},
  btn: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 4,
    marginLeft: 8,
  },
  btnCancel: {backgroundColor: '#eee'},
  btnSave: {backgroundColor: '#28a745'},
});
