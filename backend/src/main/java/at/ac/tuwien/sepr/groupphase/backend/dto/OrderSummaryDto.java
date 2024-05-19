package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderSummaryDto implements AbstractDto {
    private Long id;
    private int ticketCount;
    private long totalPrice;
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

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
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

    public static class OrderSummaryDtoBuilder {
        private int ticketCount;
        private long totalPrice;
        private List<InvoiceDto> invoices;

        private LocalDateTime dateTime;

        public OrderSummaryDtoBuilder withTicketCount(int ticketCount) {
            this.ticketCount = ticketCount;
            return this;
        }

        public OrderSummaryDtoBuilder withTotalPrice(int totalPrice) {
            this.totalPrice = totalPrice;
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
            orderSummaryDto.setTotalPrice(totalPrice);
            orderSummaryDto.setInvoices(invoices);
            orderSummaryDto.setDateTime(dateTime);
            return orderSummaryDto;
        }
    }
}
