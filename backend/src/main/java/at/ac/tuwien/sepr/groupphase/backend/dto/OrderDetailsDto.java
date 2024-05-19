package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailsDto implements AbstractDto {
    private Long id;

    private List<TicketDetailsDto> tickets;

    private ApplicationUserDto customer;

    private List<InvoiceDto> invoices;

    private LocalDateTime dateTime;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TicketDetailsDto> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDetailsDto> tickets) {
        this.tickets = tickets;
    }

    public ApplicationUserDto getCustomer() {
        return customer;
    }

    public void setCustomer(ApplicationUserDto customer) {
        this.customer = customer;
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

    public class OrderDetailsDtoBuilder {
        private List<TicketDetailsDto> tickets;
        private ApplicationUserDto customer;
        private List<InvoiceDto> invoices;
        private LocalDateTime dateTime;

        public OrderDetailsDtoBuilder withTickets(List<TicketDetailsDto> tickets) {
            this.tickets = tickets;
            return this;
        }

        public OrderDetailsDtoBuilder withCustomer(ApplicationUserDto customer) {
            this.customer = customer;
            return this;
        }

        public OrderDetailsDtoBuilder withInvoices(List<InvoiceDto> invoices) {
            this.invoices = invoices;
            return this;
        }

        public OrderDetailsDtoBuilder withDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public OrderDetailsDto build() {
            var orderDetailsDto = new OrderDetailsDto();
            orderDetailsDto.setTickets(tickets);
            orderDetailsDto.setCustomer(customer);
            orderDetailsDto.setInvoices(invoices);
            orderDetailsDto.setDateTime(dateTime);
            return orderDetailsDto;
        }
    }
}
