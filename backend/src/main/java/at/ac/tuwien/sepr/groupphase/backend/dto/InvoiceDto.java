package at.ac.tuwien.sepr.groupphase.backend.dto;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.InvoiceType;

import java.time.LocalDateTime;

public class InvoiceDto implements AbstractDto {
    private Long id;
    private InvoiceType invoiceType;
    private LocalDateTime dateTime;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public class InvoiceDtoBuilder {
        private InvoiceType invoiceType;
        private LocalDateTime dateTime;

        public InvoiceDtoBuilder withInvoiceType(InvoiceType invoiceType) {
            this.invoiceType = invoiceType;
            return this;
        }

        public InvoiceDtoBuilder withDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public InvoiceDto build() {
            var invoiceDto = new InvoiceDto();
            invoiceDto.setInvoiceType(invoiceType);
            invoiceDto.setDateTime(dateTime);
            return invoiceDto;
        }
    }
}
