# Scenario Test Cases: Buy Stock

## Use Case Reference

**Use Case:** Buy Stock  
**System:** Stock Trade Game  
**Persona:** Player

---

# Scenario Test Case: Successful Purchase of One Stock

**Test ID:** SC-01-A  
**Scenario:** Player successfully buys one affordable stock from the market table

## 1. Preconditions

Before starting the test, the tester must ensure the system is in the following state:

- **System State:** The Stock Trade game is launched and a new game has been started.
- **Data State:** The stock market table is loaded and contains available stocks such as AAPL, MSFT, GOOGL, and AMZN.
- **Player State:** The player has a cash balance of `¤1500.00` and owns `0` shares of all stocks.
- **Access:** The player is currently on the Market page.

## 2. Execution & Verification

| Step # | User Action (Act) | Expected System Response (Assert) | Actual Result | Pass/Fail |
|---|---|---|---|---|
| 1 | Select `AAPL` from the stock table. | `AAPL` is selected and the Trade section updates from `No stock selected` to the selected stock. | As expected | Pass |
| 2 | Ensure the quantity field is set to `1`. | Quantity `1` is displayed in the quantity field. | As expected | Pass |
| 3 | Check the Buy button. | The Buy button is enabled because the player can afford 1 share of AAPL. | As expected | Pass |
| 4 | Click the Buy button. | The purchase is completed successfully. | As expected | Pass |
| 5 | Check the AAPL row in the stock table. | The Owned value for AAPL increases from `0` to `1`. | As expected | Pass |
| 6 | Check the Portfolio Overview. | Cash Balance decreases by the price of 1 AAPL share, Holdings Value increases, and Net Worth is updated. | As expected | Pass |
| 7 | Navigate to the Transactions page. | A new buy transaction for AAPL is shown with quantity `1` and the correct price. | As expected | Pass |

---

# Scenario Test Case: Buy Button Disabled When Player Has No Cash

**Test ID:** SC-01-B  
**Scenario:** Player cannot buy stock when their cash balance is `¤0.00`

## 1. Preconditions

Before starting the test, the tester must ensure the system is in the following state:

- **System State:** The Stock Trade game is launched.
- **Data State:** The stock market table is loaded and contains at least one available stock.
- **Player State:** A test portfolio/save file has been prepared where the player has a cash balance of `¤0.00`.
- **Access:** The player is currently on the Market page.

## 2. Execution & Verification

| Step # | User Action (Act) | Expected System Response (Assert) | Actual Result | Pass/Fail |
|---|---|---|---|---|
| 1 | Select any stock from the stock table, for example `AAPL`. | The selected stock is highlighted and the Trade section updates to show the selected stock. | As expected | Pass |
| 2 | Ensure the quantity field is set to `1`. | Quantity `1` is displayed in the quantity field. | As expected | Pass |
| 3 | Check the Buy button. | The Buy button is disabled because the player has `¤0.00` and cannot afford the selected stock. | As expected | Pass |
| 4 | Attempt to click the Buy button, if possible. | No purchase is made. | As expected | Pass |
| 5 | Check the selected stock row in the stock table. | The Owned value for the selected stock remains unchanged. | As expected | Pass |
| 6 | Check the Portfolio Overview. | Cash Balance remains `¤0.00`, and Holdings Value and Net Worth remain unchanged. | As expected | Pass |
| 7 | Navigate to the Transactions page. | No new buy transaction has been created. | As expected | Pass |

---

# Scenario Test Case: Buy Button Disabled When No Stock Is Selected

**Test ID:** SC-01-C  
**Scenario:** Player cannot buy stock without selecting a stock first

## 1. Preconditions

Before starting the test, the tester must ensure the system is in the following state:

- **System State:** The Stock Trade game is launched and a new game has been started.
- **Data State:** The stock market table is loaded and contains available stocks.
- **Player State:** The player has a cash balance of `¤1500.00`.
- **Access:** The player is currently on the Market page.
- **Selection State:** No stock is currently selected.

## 2. Execution & Verification

| Step # | User Action (Act) | Expected System Response (Assert) | Actual Result | Pass/Fail |
|---|---|---|---|---|
| 1 | View the Trade section without selecting a stock from the table. | The Trade section displays `No stock selected`. | As expected | Pass |
| 2 | Ensure the quantity field is set to `1`. | Quantity `1` is displayed in the quantity field. | As expected | Pass |
| 3 | Check the Buy button. | The Buy button is disabled because no stock is selected. | As expected | Pass |
| 4 | Attempt to click the Buy button, if possible. | No purchase is made. | As expected | Pass |
| 5 | Check the stock table. | Owned values for all stocks remain unchanged. | As expected | Pass |
| 6 | Check the Portfolio Overview. | Cash Balance, Holdings Value, and Net Worth remain unchanged. | As expected | Pass |
| 7 | Navigate to the Transactions page. | No new buy transaction has been created. | As expected | Pass |

---

# Scenario Test Case: Successful Purchase of Multiple Shares

**Test ID:** SC-01-D  
**Scenario:** Player successfully buys multiple shares of an affordable stock

## 1. Preconditions

Before starting the test, the tester must ensure the system is in the following state:

- **System State:** The Stock Trade game is launched and a new game has been started.
- **Data State:** The stock market table is loaded and contains at least one stock where multiple shares are affordable.
- **Player State:** The player has a cash balance of `¤1500.00` and owns `0` shares of the selected stock.
- **Access:** The player is currently on the Market page.

## 2. Execution & Verification

| Step # | User Action (Act) | Expected System Response (Assert) | Actual Result | Pass/Fail |
|---|---|---|---|---|
| 1 | Select an affordable stock from the stock table, for example `AAPL`. | The selected stock is highlighted and the Trade section updates to show the selected stock. | As expected | Pass |
| 2 | Increase the quantity field to `3`. | Quantity `3` is displayed in the quantity field. | As expected | Pass |
| 3 | Check the Buy button. | The Buy button is enabled because the player can afford 3 shares of the selected stock. | As expected | Pass |
| 4 | Click the Buy button. | The purchase is completed successfully. | As expected | Pass |
| 5 | Check the selected stock row in the stock table. | The Owned value for the selected stock increases from `0` to `3`. | As expected | Pass |
| 6 | Check the Portfolio Overview. | Cash Balance decreases by `3 × selected stock price`, Holdings Value increases, and Net Worth is updated. | As expected | Pass |
| 7 | Navigate to the Transactions page. | A new buy transaction is shown with the selected stock, quantity `3`, and the correct total price. | As expected | Pass |