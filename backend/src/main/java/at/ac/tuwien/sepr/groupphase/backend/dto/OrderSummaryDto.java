package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderSummaryDto implements AbstractDto {
    private Long id;
    private int ticketCount;
    private long totalPriceNonReserved;
    private long totalPriceReserved;
    private long totalPriceOpenReserved;
    private List<InvoiceDto> invoices;

    private LocalDateTime dateTime;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

    public long getTotalPriceNonReserved() {
        return totalPriceNonReserved;
    }

    public void setTotalPriceNonReserved(long totalPriceNonReserved) {
        this.totalPriceNonReserved = totalPriceNonReserved;
    }

    public List<InvoiceDto> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<InvoiceDto> invoices) {
        this.invoices = invoices;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public long getTotalPriceReserved() {
        return totalPriceReserved;
    }

    public void setTotalPriceReserved(long totalPriceReserved) {
        this.totalPriceReserved = totalPriceReserved;
    }

    public long getTotalPriceOpenReserved() {
        return totalPriceOpenReserved;
    }

    public void setTotalPriceOpenReserved(long totalPriceOpenReserved) {
        this.totalPriceOpenReserved = totalPriceOpenReserved;
    }

    public static class OrderSummaryDtoBuilder {
        private int ticketCount;
        private long totalPriceNonReserved;
        private long totalPriceReserved;
        private long totalPriceOpenReserved;
        private List<InvoiceDto> invoices;

        private LocalDateTime dateTime;

        public OrderSummaryDtoBuilder withTicketCount(int ticketCount) {
            this.ticketCount = ticketCount;
            return this;
        }

        public OrderSummaryDtoBuilder withTotalPriceNonReserved(int totalPriceNonReserved) {
            this.totalPriceNonReserved = totalPriceNonReserved;
            return this;
        }

        public OrderSummaryDtoBuilder withTotalPriceReserved(int totalPriceReserved) {
            this.totalPriceReserved = totalPriceReserved;
            return this;
        }

        public OrderSummaryDtoBuilder withTotalPriceOpenReserved(int totalPriceOpenReserved) {
            this.totalPriceOpenReserved = totalPriceOpenReserved;
            return this;
        }

        public OrderSummaryDtoBuilder withInvoices(List<InvoiceDto> invoices) {
            this.invoices = invoices;
            return this;
        }

        public OrderSummaryDtoBuilder withDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public OrderSummaryDto build() {
            var orderSummaryDto = new OrderSummaryDto();
            orderSummaryDto.setTicketCount(ticketCount);
            orderSummaryDto.setTotalPriceNonReserved(totalPriceNonReserved);
            orderSummaryDto.setTotalPriceReserved(totalPriceReserved);
            orderSummaryDto.setTotalPriceOpenReserved(totalPriceOpenReserved);
            orderSummaryDto.setInvoices(invoices);
            orderSummaryDto.setDateTime(dateTime);
            return orderSummaryDto;
        }
    }
}
