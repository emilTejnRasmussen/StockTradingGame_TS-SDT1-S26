import entities.*;
import persistence.fileImplementation.*;
import persistence.interfaces.StockPriceHistoryDao;
import shared.configuration.AppConfig;

void main()
{
    FileUnitOfWork uow = new FileUnitOfWork("data/");
    FileStockDao fileStockDao = new FileStockDao(uow);
    FilePortfolioDao filePortfolioDao = new FilePortfolioDao(uow);
    FileOwnedStockDao fileOwnedStockDao = new FileOwnedStockDao(uow);
    FileTransactionDao fileTransactionDao = new FileTransactionDao(uow);
    FileStockPriceHistoryDao fileStockPriceHistoryDao = new FileStockPriceHistoryDao(uow);

    // fileStockDao.create(new Stock("AAPL", "Apple", BigDecimal.valueOf(20000000)));
    // fileStockDao.create(new Stock("ABC", "Another big company", BigDecimal.valueOf(20355000)));
    // fileStockDao.create(new Stock("LAL", "LALAL", BigDecimal.valueOf(35234)));

    //Portfolio p = new Portfolio(UUID.randomUUID(), BigDecimal.valueOf(AppConfig.getInstance().getStartingBalance()));
    //filePortfolioDao.create(p);

    // p.setCurrentBalance(BigDecimal.valueOf(200000));
    // filePortfolioDao.update(p);

    // fileOwnedStockDao.create(new OwnedStock(filePortfolioDao.getAll().getFirst().getId(), fileStockDao.getAll().getFirst().getSymbol(), 10 ));
//    OwnedStock os = fileOwnedStockDao.getAll().getFirst();
//    os.addShares(20);
//    fileOwnedStockDao.update(os);

//    s.setCurrentPrice(BigDecimal.valueOf(50250));
//    fileStockDao.update(s);
//    Transaction t = new Transaction(UUID.randomUUID(), uow.getPortfolios().getFirst().getId(), s.getSymbol(),
//            Transaction.Type.SELL, 5, s.getCurrentPrice(), BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee()),
//            LocalDateTime.now());
//
//    fileTransactionDao.create(t);
    Stock s = uow.getStocks().getFirst();
    StockPriceHistory stockPriceHistory = new StockPriceHistory(UUID.randomUUID(), s.getSymbol(), s.getCurrentPrice(), LocalDateTime.now());

    fileStockPriceHistoryDao.create(stockPriceHistory);

    System.out.println("--- Stock ---");
    for (Stock stock : fileStockDao.getAll())
    {
        System.out.println(stock.getSymbol() + " " + stock.getName());
    }
    System.out.println("--- Portfolios ---");
    for (Portfolio portfolio : filePortfolioDao.getAll())
    {
        System.out.println(portfolio.getId() + " " + portfolio.getCurrentBalance());

    }
    System.out.println("--- OwnedStock ---");
    for (OwnedStock ownedStock : uow.getOwnedStocks())
    {
        Stock st = fileStockDao.getBySymbol(ownedStock.getStockSymbol()).orElse(null);
        System.out.println(ownedStock.getId() + " " + ownedStock.getStockSymbol() + " " + st.getName() + " "
                + ownedStock.getNumberOfShares() + " " + ownedStock.getPortfolioId());
    }
    System.out.println("--- Transactions ---");
    for (Transaction transaction : uow.getTransactions())
    {
        System.out.printf(
                "Transaction - [%s]%n" +
                        "Fee: %s%n" +
                        "Quantity: %s%n" +
                        "Price/share: %s%n" +
                        "Total: %s%n" +
                        "Type: %s%n",
                transaction.timeStamp(), transaction.fee(), transaction.quantity(), transaction.pricePerShare(),
                transaction.getTotalPriceWithFee(), transaction.type());
    }
    System.out.println("--- StockPriceHistory ---");
    for (StockPriceHistory priceHistory : uow.getStockPriceHistories())
    {
        {
            System.out.printf(
                    "hist - [%s]%n" +
                            "Stock: %s%n" +
                            "Price: %s%n"
                    , priceHistory.timeStamp(), priceHistory.stockSymbol(), priceHistory.price());
        }
    }


}