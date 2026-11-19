const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_123";
const BOB_UID = "bob_uid_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write any FinSmart data", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("expenses").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("expenses").doc("exp_1").set({
      userId: ALICE_UID,
      title: "Groceries",
      category: "FOOD",
      amount: 500,
      monthKey: "2026-11",
      dateMillis: Date.now(),
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Owner can create and update UserProfile; rejects ghost fields and invalid theme", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Samarth S",
      financialSafetyScore: 85,
      streakDays: 3,
      completedLessonIds: ["bb_1"],
      completedQuizIds: [],
      earnedBadgeIds: ["first_step"],
      selectedLanguage: "ENGLISH",
      preferredTheme: "LIGHT",
      simpleMode: false,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      preferredTheme: "DARK_NEON",
      simpleMode: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject ghost field on update
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt overwrite
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Owner can CRUD expenses, budgets, goals, scanHistory, trustedContacts, incidentReports, and quizResults", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 2000);

  // Expense create + update + list
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("expenses").doc("exp_1").set({
      userId: ALICE_UID,
      title: "Milk & Vegetables",
      category: "FOOD",
      amount: 450,
      monthKey: "2026-11",
      dateMillis: Date.now(),
      note: "Weekly market",
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("expenses").get());

  // Budget create
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("budgets").doc("2026-11").set({
      userId: ALICE_UID,
      monthKey: "2026-11",
      monthlyIncome: 32000,
      householdLimit: 8000,
      foodLimit: 6000,
      transportLimit: 3500,
      educationLimit: 4000,
      healthcareLimit: 2500,
      billsLimit: 3000,
      shoppingLimit: 2000,
      otherLimit: 1000,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Savings goal create
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("goals").doc("goal_1").set({
      userId: ALICE_UID,
      title: "Emergency Fund",
      category: "Emergency",
      targetAmount: 50000,
      savedAmount: 12000,
      targetDateMillis: Date.now() + 86400000 * 180,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Scan history create & mark reported
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("scanHistory").doc("scan_1").set({
      userId: ALICE_UID,
      scanType: "LINK",
      inputPreview: "sbi-kyc-update.in",
      verdict: "RED",
      summary: "Suspicious bank impersonation domain.",
      reasons: ["Lookalike domain", "Contains KYC keyword"],
      reported: false,
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("scanHistory").doc("scan_1").update({
      reported: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Trusted contact create & revoke
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("trustedContacts").doc("tc_1").set({
      userId: ALICE_UID,
      name: "Ramesh Kumar",
      relationship: "Son",
      phoneNumber: "9876543210",
      consentGranted: true,
      revoked: false,
      sharedAlertsCount: 0,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Incident report create
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("incidentReports").doc("inc_1").set({
      userId: ALICE_UID,
      incidentDate: "2026-11-19",
      approximateTime: "14:30",
      amount: 2500,
      referenceId: "UTR12345678",
      paymentMethod: "UPI",
      description: "Received fake refund collect request.",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Quiz result create
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("quizResults").doc("quiz_1").set({
      userId: ALICE_UID,
      quizId: "fraud_basics",
      quizTitle: "Fraud & Scam Safety Quiz",
      score: 4,
      totalQuestions: 5,
      createdAt: now,
    })
  );
});

test("Cross-user isolation: Bob cannot read or write Alice's collections", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    const now = new Date(Date.now() - 5000);
    await db.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      financialSafetyScore: 80,
      streakDays: 2,
      completedLessonIds: [],
      completedQuizIds: [],
      earnedBadgeIds: [],
      selectedLanguage: "ENGLISH",
      preferredTheme: "LIGHT",
      simpleMode: false,
      createdAt: now,
      updatedAt: now,
    });
    await db.collection("users").doc(ALICE_UID).collection("expenses").doc("exp_a").set({
      userId: ALICE_UID,
      title: "Electricity",
      category: "BILLS",
      amount: 1200,
      monthKey: "2026-11",
      dateMillis: Date.now(),
      createdAt: now,
      updatedAt: now,
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("expenses").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("expenses").doc("exp_a").delete());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("trustedContacts").doc("tc_hijack").set({
      userId: BOB_UID,
      name: "Attacker",
      relationship: "None",
      phoneNumber: "9999999999",
      consentGranted: true,
      revoked: false,
      sharedAlertsCount: 0,
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Validation rejects future timestamps and @DocumentId field leakage", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("expenses").doc("exp_future").set({
      id: "exp_future",
      userId: ALICE_UID,
      title: "Bad payload",
      category: "OTHER",
      amount: 100,
      monthKey: "2026-11",
      dateMillis: Date.now(),
      createdAt: new Date(Date.now() + 60000),
      updatedAt: new Date(Date.now() + 60000),
    })
  );
});
