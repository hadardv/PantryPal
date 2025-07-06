import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  ScrollView,
  Dimensions,
  StyleSheet
} from "react-native";
import { PieChart, BarChart } from "react-native-chart-kit";
import axios from "axios";

export default function ViewTrendsScreen({ route }) {
  const { user } = route.params;
  const windowWidth = Dimensions.get("window").width;
  const chartWidth = windowWidth - 72;
  const BASE_URL = "http://10.100.102.4:8084/ambient-intelligence";
      const USER_BASE = 
  "http://10.100.102.4:8084/ambient-intelligence/users";

  const [totalCount, setTotalCount]     = useState(0);
  const [expiredCount, setExpiredCount] = useState(0);
  const [weeklyByDay, setWeeklyByDay]   = useState([]);   
  const [lowStockCount, setLowStockCount] = useState(0);

  useEffect(() => {
    fetchTotals();
    fetchWeekly();
    fetchLowStock();
    console.log("weekly by days",weeklyByDay)
    console.log("total count", totalCount);
    console.log("expired count",expiredCount);
    console.log("low stock count", lowStockCount)
  }, []);

  async function fetchTotals() {
    try {
      const params = {
        userSystemID: user.userId.systemID,
        userEmail:    user.userId.email
      };
      const res = await axios.get(`${BASE_URL}/objects`, { params });
      const items = res.data.filter(
        o => ["PRODUCT_BY_WEIGHT", "PRODUCT_BY_QUANTITY"].includes(o.type)
      );
      const now = Date.now();
      const expired = items.filter(
        o => new Date(o.objectDetails.expiration).getTime() < now
      );
      setTotalCount(items.length);
      setExpiredCount(expired.length);
    } catch (err) {
      console.error("fetchTotals error:", err);
    }
  }

  async function fetchWeekly() {
    try {
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

      if(user.role === 'OPERATOR') {
        const endRes = await axios.put(endUserUrl, bodyEndUser);

      const payload = {
        commandId:        { commandId: "", systemID: user.userId.systemID },
        command:          "getProductsExpiringInNextWeek",
        targetObject:     { objectId: "", systemID: "" },
        invocationTimestamp: new Date().toISOString(),
        invokedBy:        { email: user.userId.email, systemID: user.userId.systemID },
        commandAttributes:{}
      };
      const res = await axios.post(
        `${BASE_URL}/commands`,
        payload,
        { headers: { "Content-Type": "application/json" }}
      );
      console.log("expired this week",res.data)
      const counts = {};
      res.data.forEach(o => {
        const day = new Date(o.objectDetails.expiration)
          .toLocaleDateString("en-US",{ weekday:"short" });
        counts[day] = (counts[day] || 0) + 1;
      });
      const weekOrder = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
      setWeeklyByDay(weekOrder.map(d => ({ day: d, count: counts[d] || 0 })));

      const opRes = await axios.put(operatorUrl, bodyOperator);
    } else {
       const payload = {
        commandId:        { commandId: "", systemID: user.userId.systemID },
        command:          "getProductsExpiringInNextWeek",
        targetObject:     { objectId: "", systemID: "" },
        invocationTimestamp: new Date().toISOString(),
        invokedBy:        { email: user.userId.email, systemID: user.userId.systemID },
        commandAttributes:{}
      };
      const res = await axios.post(
        `${BASE_URL}/commands`,
        payload,
        { headers: { "Content-Type": "application/json" }}
      );
      console.log("expired this week",res.data)
      const counts = {};
      res.data.forEach(o => {
        const day = new Date(o.objectDetails.expiration)
          .toLocaleDateString("en-US",{ weekday:"short" });
        counts[day] = (counts[day] || 0) + 1;
      });
      const weekOrder = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
      setWeeklyByDay(weekOrder.map(d => ({ day: d, count: counts[d] || 0 })));
    }
    } catch (err) {
      console.error("fetchWeekly error:", err);
    }
  }

  async function fetchLowStock() {
    try {
      const params = {
        userSystemID: user.userId.systemID,
        userEmail:    user.userId.email
      };
      const res = await axios.get(`${BASE_URL}/objects`, { params });
      // threshold of 2 or less:
      const low = res.data.filter(
        o => o.objectDetails.amount <= 2
      );
      console.log(low)
      setLowStockCount(low.length);
    } catch (err) {
      console.error("fetchLowStock error:", err);
    }
  }

  return (
    <ScrollView style={styles.wrapper} contentContainerStyle={styles.container}>
      <Text style={styles.title}>Trends & Analytics</Text>

      {/* ── PIE: In-Stock vs Expired ──────────────────────────────────────── */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>In-Stock vs Expired</Text>
        <PieChart
          data={[
            {
              name:       "In Stock",
              population: totalCount - expiredCount,
              color:      "#4CAF50",
              legendFontColor: "#333",
              legendFontSize: 14
            },
            {
              name:       "Expired",
              population: expiredCount,
              color:      "#F44336",
              legendFontColor: "#333",
              legendFontSize: 14
            }
          ]}
          width={chartWidth}
          height={180}
          chartConfig={chartConfig}
          accessor="population"
          backgroundColor="transparent"
          paddingLeft="15"
          absolute
          style={{ marginVertical: 8, alignSelf: "center" }}
        />
      </View>

      {/* ── BAR: Expiring This Week ─────────────────────────────────────── */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>Products Expiring This Week</Text>

    {weeklyByDay.length > 0 ? (
      <ScrollView
      horizontal
      showsHorizontalScrollIndicator={false}
      contentContainerStyle={{ paddingHorizontal: 8 }}
       >
          <BarChart
            data={{
              labels:   weeklyByDay.map(d => d.day),
              datasets:[{ data: weeklyByDay.map(d => d.count) }]
            }}
            width={weeklyByDay.length * 50}
            height={200}
            chartConfig={chartConfig}
            fromZero
            showValuesOnTopOfBars
            barPercentage={0.6}
            style={{ marginVertical: 8 }}
            verticalLabelRotation={0}
          />
          </ScrollView>
        ) : (
          <Text style={styles.noData}>No items expiring this week</Text>
        )}
      </View>

      {/* ── KPI: Low-Stock Items ──────────────────────────────────────────── */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>Low-Stock Items</Text>
        <Text style={styles.kpi}>{lowStockCount}</Text>
      </View>
    </ScrollView>
  );
}

const chartConfig = {
  backgroundGradientFrom: "#ffff",
  backgroundGradientTo:   "#ffff",
  decimalPlaces:          0,
  color:      (opacity=1) => `rgba(33,150,243,${opacity})`,   
  labelColor: (opacity=1) => `rgba(33,33,33,${opacity})`,    
  propsForBackgroundLines: {
    strokeDasharray: [4,8]     
  }
};


const styles = StyleSheet.create({
  wrapper: { flex: 1, backgroundColor: "#f2f2f2" },
  container:{ padding: 20 },
  title:    { fontSize: 24, fontWeight: "bold", marginBottom: 16, textAlign: "center" },

  card:     {
    backgroundColor: "#fff",
    borderRadius: 8,
    padding: 16,
    marginBottom: 16,
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowRadius: 8,
    elevation: 2
  },
  cardTitle: { fontSize: 18, fontWeight: "600", marginBottom: 4 },
  noData:    { textAlign: "center", color: "#999", paddingVertical: 16 },
  kpi:       { fontSize: 48, fontWeight: "bold", color: "#FF9800", textAlign: "center", marginTop: 12 }
});
